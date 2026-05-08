import json
from django.test import TestCase, Client
from .models import Editor
from .views_v2 import hash_password


class AuthenticationTests(TestCase):
    def setUp(self):
        self.client = Client()
        self.register_url = '/api/v2.0/editors'
        self.login_url = '/api/v2.0/login'

    def test_register_editor(self):
        """Test user registration"""
        data = {
            'login': 'testuser',
            'password': 'testpassword123',
            'firstname': 'Test',
            'lastname': 'User',
            'role': 'CUSTOMER'
        }
        response = self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        self.assertEqual(response.status_code, 201)
        self.assertEqual(response.json()['login'], 'testuser')
        self.assertEqual(response.json()['role'], 'CUSTOMER')

    def test_register_duplicate_login(self):
        """Test that duplicate login returns 403"""
        data = {
            'login': 'duplicate',
            'password': 'testpassword123',
            'firstname': 'Test',
            'lastname': 'User',
        }
        self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        response = self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        self.assertEqual(response.status_code, 403)

    def test_login_success(self):
        """Test successful login returns access_token"""
        # Register
        data = {
            'login': 'logintest',
            'password': 'testpassword123',
            'firstname': 'Test',
            'lastname': 'User',
        }
        self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        # Login
        login_data = {'login': 'logintest', 'password': 'testpassword123'}
        response = self.client.post(
            self.login_url,
            data=json.dumps(login_data),
            content_type='application/json'
        )
        self.assertEqual(response.status_code, 200)
        self.assertIn('access_token', response.json())

    def test_login_wrong_password(self):
        """Test login with wrong password returns 401"""
        # Register
        data = {
            'login': 'wrongpwd',
            'password': 'testpassword123',
            'firstname': 'Test',
            'lastname': 'User',
        }
        self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        # Login with wrong password
        login_data = {'login': 'wrongpwd', 'password': 'wrongpassword'}
        response = self.client.post(
            self.login_url,
            data=json.dumps(login_data),
            content_type='application/json'
        )
        self.assertEqual(response.status_code, 401)

    def test_protected_endpoint_without_token(self):
        """Test that protected endpoints return 401 without token"""
        response = self.client.get('/api/v2.0/editors')
        self.assertEqual(response.status_code, 401)

    def test_protected_endpoint_with_valid_token(self):
        """Test that protected endpoints work with valid token"""
        # Register
        data = {
            'login': 'authuser',
            'password': 'testpassword123',
            'firstname': 'Auth',
            'lastname': 'User',
        }
        self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        # Login
        login_data = {'login': 'authuser', 'password': 'testpassword123'}
        response = self.client.post(
            self.login_url,
            data=json.dumps(login_data),
            content_type='application/json'
        )
        token = response.json()['access_token']

        # Access protected resource
        response = self.client.get(
            '/api/v2.0/editors',
            HTTP_AUTHORIZATION=f'Bearer {token}'
        )
        self.assertEqual(response.status_code, 200)

    def test_protected_endpoint_with_invalid_token(self):
        """Test that protected endpoints reject invalid tokens"""
        response = self.client.get(
            '/api/v2.0/editors',
            HTTP_AUTHORIZATION='Bearer invalidtoken123'
        )
        self.assertEqual(response.status_code, 401)

    def test_v1_endpoints_no_auth_required(self):
        """Test that v1.0 endpoints work without authentication"""
        # Create an editor directly
        Editor.objects.create(
            login='v1editor',
            password='plaintext123',
            firstname='V1',
            lastname='Editor'
        )
        response = self.client.get('/api/v1.0/editors')
        self.assertEqual(response.status_code, 200)


class RoleBasedAccessTests(TestCase):
    def setUp(self):
        self.client = Client()
        self.register_url = '/api/v2.0/editors'
        self.login_url = '/api/v2.0/login'

        # Register admin
        admin_data = {
            'login': 'adminuser',
            'password': 'adminpass123',
            'firstname': 'Admin',
            'lastname': 'User',
            'role': 'ADMIN'
        }
        self.client.post(
            self.register_url,
            data=json.dumps(admin_data),
            content_type='application/json'
        )
        login_resp = self.client.post(
            self.login_url,
            data=json.dumps({'login': 'adminuser', 'password': 'adminpass123'}),
            content_type='application/json'
        )
        self.admin_token = login_resp.json()['access_token']

        # Register customer
        customer_data = {
            'login': 'customeruser',
            'password': 'custpass123',
            'firstname': 'Customer',
            'lastname': 'User',
            'role': 'CUSTOMER'
        }
        self.client.post(
            self.register_url,
            data=json.dumps(customer_data),
            content_type='application/json'
        )
        login_resp = self.client.post(
            self.login_url,
            data=json.dumps({'login': 'customeruser', 'password': 'custpass123'}),
            content_type='application/json'
        )
        self.customer_token = login_resp.json()['access_token']

    def test_admin_can_list_editors(self):
        response = self.client.get(
            '/api/v2.0/editors',
            HTTP_AUTHORIZATION=f'Bearer {self.admin_token}'
        )
        self.assertEqual(response.status_code, 200)

    def test_customer_can_list_editors(self):
        response = self.client.get(
            '/api/v2.0/editors',
            HTTP_AUTHORIZATION=f'Bearer {self.customer_token}'
        )
        self.assertEqual(response.status_code, 200)

    def test_admin_can_delete_any_editor(self):
        # Create a new editor to delete
        data = {
            'login': 'todelete',
            'password': 'deletepass123',
            'firstname': 'To',
            'lastname': 'Delete',
        }
        resp = self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        editor_id = resp.json()['id']

        response = self.client.delete(
            f'/api/v2.0/editors/{editor_id}',
            HTTP_AUTHORIZATION=f'Bearer {self.admin_token}'
        )
        self.assertEqual(response.status_code, 204)

    def test_customer_cannot_delete_other_editor(self):
        # Create another editor
        data = {
            'login': 'othereditor',
            'password': 'otherpass123',
            'firstname': 'Other',
            'lastname': 'Editor',
        }
        resp = self.client.post(
            self.register_url,
            data=json.dumps(data),
            content_type='application/json'
        )
        editor_id = resp.json()['id']

        response = self.client.delete(
            f'/api/v2.0/editors/{editor_id}',
            HTTP_AUTHORIZATION=f'Bearer {self.customer_token}'
        )
        self.assertEqual(response.status_code, 403)

    def test_admin_can_create_labels(self):
        data = {'name': 'newlabel'}
        response = self.client.post(
            '/api/v2.0/labels',
            data=json.dumps(data),
            content_type='application/json',
            HTTP_AUTHORIZATION=f'Bearer {self.admin_token}'
        )
        self.assertEqual(response.status_code, 201)

    def test_customer_cannot_create_labels(self):
        data = {'name': 'forbidden_label'}
        response = self.client.post(
            '/api/v2.0/labels',
            data=json.dumps(data),
            content_type='application/json',
            HTTP_AUTHORIZATION=f'Bearer {self.customer_token}'
        )
        self.assertEqual(response.status_code, 403)

    def test_customer_can_read_labels(self):
        # Admin creates a label
        data = {'name': 'readlabel'}
        self.client.post(
            '/api/v2.0/labels',
            data=json.dumps(data),
            content_type='application/json',
            HTTP_AUTHORIZATION=f'Bearer {self.admin_token}'
        )
        # Customer can read
        response = self.client.get(
            '/api/v2.0/labels',
            HTTP_AUTHORIZATION=f'Bearer {self.customer_token}'
        )
        self.assertEqual(response.status_code, 200)

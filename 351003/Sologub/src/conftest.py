import pytest
from testcontainers.postgres import PostgresContainer
from rest_framework.test import APIClient
from django.conf import settings

@pytest.fixture(scope="session")
def postgres_container():
    with PostgresContainer("postgres:15-alpine") as postgres:
        settings.DATABASES['default'] = {
            'ENGINE': 'django.db.backends.postgresql',
            'NAME': postgres.dbname,
            'USER': postgres.username,
            'PASSWORD': postgres.password,
            'HOST': postgres.get_container_host_ip(),
            'PORT': postgres.get_exposed_port(5432),
            'OPTIONS': {'options': '-c search_path=distcomp,public'}
        }
        yield postgres

@pytest.fixture
def api_client(db, postgres_container):
    return APIClient()

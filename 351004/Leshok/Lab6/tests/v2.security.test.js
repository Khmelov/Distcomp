// tests/v2.security.test.js
const request = require('supertest');
const { app } = require('../server');

describe('API v2.0 (JWT + роли)', () => {
  let adminToken, customerToken, customerId;

  beforeAll(async () => {
    // Регистрация admin
    await request(app)
      .post('/api/v2.0/creators')
      .send({ login: 'admin2', password: 'admin123', firstName: 'Admin', lastName: 'User', role: 'ADMIN' });
    // Регистрация customer
    const custReg = await request(app)
      .post('/api/v2.0/creators')
      .send({ login: 'cust2', password: 'pass', firstName: 'Cust', lastName: 'User', role: 'CUSTOMER' });
    customerId = custReg.body.id;
    // Логины
    const adminLogin = await request(app)
      .post('/api/v2.0/login')
      .send({ login: 'admin2', password: 'admin123' });
    adminToken = adminLogin.body.access_token;
    const custLogin = await request(app)
      .post('/api/v2.0/login')
      .send({ login: 'cust2', password: 'pass' });
    customerToken = custLogin.body.access_token;
  });

  test('Доступ без токена -> 401', async () => {
    const res = await request(app).get('/api/v2.0/news');
    expect(res.status).toBe(401);
  });

  test('ADMIN может получить всех creators', async () => {
    const res = await request(app)
      .get('/api/v2.0/creators')
      .set('Authorization', `Bearer ${adminToken}`);
    expect(res.status).toBe(200);
    expect(res.body.length).toBeGreaterThanOrEqual(2);
  });

  test('CUSTOMER НЕ может получить всех creators', async () => {
    const res = await request(app)
      .get('/api/v2.0/creators')
      .set('Authorization', `Bearer ${customerToken}`);
    expect(res.status).toBe(403);
  });

  test('CUSTOMER может получить свой профиль', async () => {
    const res = await request(app)
      .get(`/api/v2.0/creators/${customerId}`)
      .set('Authorization', `Bearer ${customerToken}`);
    expect(res.status).toBe(200);
    expect(res.body.login).toBe('cust2');
  });

  test('Создание новости от имени CUSTOMER', async () => {
    const res = await request(app)
      .post('/api/v2.0/news')
      .set('Authorization', `Bearer ${customerToken}`)
      .send({ title: 'My News', content: 'Hello', creatorId: customerId });
    expect(res.status).toBe(201);
    expect(res.body.creatorId).toBe(customerId);
  });
});
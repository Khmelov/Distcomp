// tests/v1.crud.test.js
const request = require('supertest');
const { app } = require('../server');

describe('API v1.0 (без аутентификации)', () => {
  let createdCreatorId, createdNewsId;

  test('POST /api/v1.0/creators -> 201', async () => {
    const res = await request(app)
      .post('/api/v1.0/creators')
      .send({ login: 'testuser', password: '123', firstName: 'Test', lastName: 'User', role: 'CUSTOMER' });
    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('id');
    createdCreatorId = res.body.id;
  });

  test('GET /api/v1.0/creators -> 200', async () => {
    const res = await request(app).get('/api/v1.0/creators');
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
  });

  test('POST /api/v1.0/news -> 201', async () => {
    const res = await request(app)
      .post('/api/v1.0/news')
      .send({ title: 'Test News', content: 'Content', creatorId: createdCreatorId });
    expect(res.status).toBe(201);
    createdNewsId = res.body.id;
  });

  test('DELETE /api/v1.0/news/:id -> 204', async () => {
    const res = await request(app).delete(`/api/v1.0/news/${createdNewsId}`);
    expect(res.status).toBe(204);
  });
});
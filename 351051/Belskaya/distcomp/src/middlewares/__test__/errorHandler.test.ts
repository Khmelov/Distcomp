import request from 'supertest';
import app from '../../app';

describe('ErrorHandler Middleware', () => {
  test('должен возвращать 400 при невалидном JSON', async () => {
    const res = await request(app)
      .post('/api/v1.0/authors')
      .set('Content-Type', 'application/json')
      .send('{ "login": "test", "password": ');

    expect(res.status).toBe(400);
    expect(res.body.errorCode).toBe('40002');
    expect(res.body.errorMessage).toContain('Invalid JSON');
  });
});

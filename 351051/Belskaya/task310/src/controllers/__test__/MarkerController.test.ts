import request from 'supertest';
import app from '../../app';
import { resetRepositories } from '../../repositories';

describe('MarkerController', () => {
  beforeEach(() => {
    resetRepositories();
  });

  test('POST /markers — 201', async () => {
    const res = await request(app)
      .post('/api/v1.0/markers')
      .send({ name: 'urgent' });

    expect(res.status).toBe(201);
    expect(res.body.name).toBe('urgent');
  });

  test('POST /markers — 400 при коротком name', async () => {
    const res = await request(app)
      .post('/api/v1.0/markers')
      .send({ name: 'a' });

    expect(res.status).toBe(400);
    expect(res.body.errorCode).toBe('40001');
  });

  test('POST /markers — 409 при дубликате name', async () => {
    await request(app).post('/api/v1.0/markers').send({ name: 'urgent' });
    const res = await request(app)
      .post('/api/v1.0/markers')
      .send({ name: 'urgent' });

    expect(res.status).toBe(409);
    expect(res.body.errorCode).toBe('40901');
  });

  test('GET /markers/:id — 200, успешное получение', async () => {
    await request(app).post('/api/v1.0/markers').send({ name: 'urgent' });
    const res = await request(app).get('/api/v1.0/markers/1');

    expect(res.status).toBe(200);
    expect(res.body.name).toBe('urgent');
  });

  test('GET /markers/:id — 404 для несуществующего', async () => {
    const res = await request(app).get('/api/v1.0/markers/999');

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40403');
  });

  test('GET /markers/by-topic/:topicId — возвращает маркеры темы', async () => {
    await request(app).post('/api/v1.0/authors').send({
      login: 'author@example.com',
      password: 'password123',
      firstname: 'Иван',
      lastname: 'Иванов',
    });
    await request(app).post('/api/v1.0/topics').send({
      authorId: 1,
      title: 'Topic',
      content: 'Content',
    });

    const res = await request(app).get('/api/v1.0/markers/by-topic/1');

    expect(res.status).toBe(200);
    expect(res.body).toEqual([]);
  });

  test('GET /markers/by-topic/:topicId — 404, если темы нет', async () => {
    const res = await request(app).get('/api/v1.0/markers/by-topic/999');
    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40402');
  });

  test('PUT /markers/:id — 200, успешное обновление', async () => {
    await request(app).post('/api/v1.0/markers').send({ name: 'urgent' });
    const res = await request(app)
      .put('/api/v1.0/markers/1')
      .send({ name: 'important' });

    expect(res.status).toBe(200);
    expect(res.body.name).toBe('important');
  });

  test('PUT /markers/:id — 404 при обновлении несуществующего', async () => {
    const res = await request(app)
      .put('/api/v1.0/markers/999')
      .send({ name: 'important' });

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40403');
  });

  test('DELETE /markers/:id — 204, успешное удаление', async () => {
    await request(app).post('/api/v1.0/markers').send({ name: 'urgent' });
    const res = await request(app).delete('/api/v1.0/markers/1');

    expect(res.status).toBe(204);
  });

  test('DELETE /markers/:id — 404 при удалении несуществующего', async () => {
    const res = await request(app).delete('/api/v1.0/markers/999');

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40403');
  });
});

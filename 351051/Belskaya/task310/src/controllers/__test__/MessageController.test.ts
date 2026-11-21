import request from 'supertest';
import app from '../../app';
import { resetRepositories } from '../../repositories';

describe('MessageController', () => {
  beforeEach(async () => {
    resetRepositories();
    await request(app).post('/api/v1.0/authors').send({
      login: 'author@example.com',
      password: 'password123',
      firstname: 'Иван',
      lastname: 'Иванов',
    });
    await request(app).post('/api/v1.0/topics').send({
      authorId: 1,
      title: 'Topic',
      content: 'Content here',
    });
  });

  test('POST /messages — 201', async () => {
    const res = await request(app)
      .post('/api/v1.0/messages')
      .send({ topicId: 1, content: 'Hello world' });

    expect(res.status).toBe(201);
    expect(res.body.message.content).toBe('Hello world');
  });

  test('POST /messages — 422 при несуществующем topicId', async () => {
    const res = await request(app)
      .post('/api/v1.0/messages')
      .send({ topicId: 999, content: 'Hello' });

    expect(res.status).toBe(422);
    expect(res.body.errorCode).toBe('42203');
  });

  test('GET /messages/:id — 404 для несуществующего', async () => {
    const res = await request(app).get('/api/v1.0/messages/999');

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40404');
  });

  test('GET /messages/by-topic/:topicId — возвращает сообщения темы', async () => {
    await request(app).post('/api/v1.0/messages').send({
      topicId: 1,
      content: 'First message',
    });
    await request(app).post('/api/v1.0/messages').send({
      topicId: 1,
      content: 'Second message',
    });

    const res = await request(app).get('/api/v1.0/messages/by-topic/1');

    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(2);
    expect(res.body[0].message.content).toBe('First message');
  });

  test('GET /messages/by-topic/:topicId — 404, если темы нет', async () => {
    const res = await request(app).get('/api/v1.0/messages/by-topic/999');
    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40402');
  });
});

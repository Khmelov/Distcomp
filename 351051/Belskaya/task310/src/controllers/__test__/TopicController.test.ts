import request from 'supertest';
import app from '../../app';
import { resetRepositories } from '../../repositories';

describe('TopicController', () => {
  beforeEach(async () => {
    resetRepositories();
    await request(app).post('/api/v1.0/authors').send({
      login: 'author@example.com',
      password: 'password123',
      firstname: 'Иван',
      lastname: 'Иванов',
    });
  });

  const validTopic = {
    authorId: 1,
    title: 'First Topic',
    content: 'Some content',
  };

  test('POST /topics — 201, created/modified в ISO8601', async () => {
    const res = await request(app).post('/api/v1.0/topics').send(validTopic);

    expect(res.status).toBe(201);
    expect(res.body.topic.id).toBe(1);
    expect(res.body.topic.created).toMatch(/^\d{4}-\d{2}-\d{2}T/);
    expect(res.body.topic.modified).toMatch(/^\d{4}-\d{2}-\d{2}T/);
  });

  test('POST /topics — 422 при несуществующем authorId', async () => {
    const res = await request(app)
      .post('/api/v1.0/topics')
      .send({ ...validTopic, authorId: 999 });

    expect(res.status).toBe(422);
    expect(res.body.errorCode).toBe('42202');
  });

  test('POST /topics — 400 при коротком title', async () => {
    const res = await request(app)
      .post('/api/v1.0/topics')
      .send({ ...validTopic, title: 'a' });

    expect(res.status).toBe(400);
    expect(res.body.errorCode).toBe('40001');
  });

  test('GET /topics/:id — 404 для несуществующего', async () => {
    const res = await request(app).get('/api/v1.0/topics/999');

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40402');
  });

  test('DELETE /topics/:id — 204, затем 404', async () => {
    await request(app).post('/api/v1.0/topics').send(validTopic);
    const del = await request(app).delete('/api/v1.0/topics/1');
    expect(del.status).toBe(204);

    const get = await request(app).get('/api/v1.0/topics/1');
    expect(get.status).toBe(404);
  });

  test('GET /topics/by-filters — фильтр по title', async () => {
    await request(app).post('/api/v1.0/topics').send({
      authorId: 1,
      title: 'JavaScript Basics',
      content: 'Content 1',
    });
    await request(app).post('/api/v1.0/topics').send({
      authorId: 1,
      title: 'TypeScript Guide',
      content: 'Content 2',
    });

    const res = await request(app).get(
      '/api/v1.0/topics/by-filters?title=Script'
    );
    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(2);
  });

  test('GET /topics/by-filters — фильтр по authorLogin', async () => {
    await request(app).post('/api/v1.0/topics').send({
      authorId: 1,
      title: 'Topic 1',
      content: 'Content',
    });

    const res = await request(app).get(
      '/api/v1.0/topics/by-filters?authorLogin=author@example.com'
    );
    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(1);
  });

  test('GET /topics/by-filters — пустой результат, если автор не найден', async () => {
    const res = await request(app).get(
      '/api/v1.0/topics/by-filters?authorLogin=nobody'
    );
    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(0);
  });
});

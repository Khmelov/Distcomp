import request from 'supertest';
import app from '../../app';
import { resetRepositories, topicMarkerRepository } from '../../repositories';

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

  test('GET /topics/:id — 200, успешное получение', async () => {
    await request(app).post('/api/v1.0/topics').send(validTopic);
    const res = await request(app).get('/api/v1.0/topics/1');

    expect(res.status).toBe(200);
    expect(res.body.topic.title).toBe('First Topic');
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

  test('DELETE /topics/:id — 404 при удалении несуществующего', async () => {
    const res = await request(app).delete('/api/v1.0/topics/999');
    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40402');
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

  test('GET /topics/by-filters — фильтр по markerNames и markerIds', async () => {
    await request(app).post('/api/v1.0/markers').send({ name: 'test-marker' });

    await request(app).post('/api/v1.0/topics').send(validTopic);

    topicMarkerRepository.addLink(1, 1);

    const res1 = await request(app).get(
      '/api/v1.0/topics/by-filters?markerNames=test-marker'
    );
    expect(res1.status).toBe(200);
    expect(res1.body).toHaveLength(1);

    const res2 = await request(app).get(
      '/api/v1.0/topics/by-filters?markerIds=1'
    );
    expect(res2.status).toBe(200);
    expect(res2.body).toHaveLength(1);

    const res3 = await request(app).get(
      '/api/v1.0/topics/by-filters?markerNames=unknown'
    );
    expect(res3.status).toBe(200);
    expect(res3.body).toHaveLength(0);

    const res4 = await request(app).get(
      '/api/v1.0/topics/by-filters?markerIds=999'
    );
    expect(res4.status).toBe(200);
    expect(res4.body).toHaveLength(0);
  });

  test('GET /topics/by-filters — фильтр по content', async () => {
    await request(app).post('/api/v1.0/topics').send(validTopic);
    const res = await request(app).get(
      '/api/v1.0/topics/by-filters?content=Some'
    );
    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(1);
  });

  test('PUT /topics/:id — 200, успешное обновление', async () => {
    await request(app).post('/api/v1.0/topics').send(validTopic);
    const res = await request(app)
      .put('/api/v1.0/topics/1')
      .send({ ...validTopic, title: 'Updated Title' });

    expect(res.status).toBe(200);
    expect(res.body.topic.title).toBe('Updated Title');
  });

  test('PUT /topics/:id — 404 при обновлении несуществующего', async () => {
    const res = await request(app).put('/api/v1.0/topics/999').send(validTopic);

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40402');
  });
});

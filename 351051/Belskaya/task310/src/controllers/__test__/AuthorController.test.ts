import request from 'supertest';
import app from '../../app';
import { resetRepositories } from '../../repositories';

describe('AuthorController', () => {
  beforeEach(() => {
    resetRepositories();
  });

  const validAuthor = {
    login: 'test@example.com',
    password: 'password123',
    firstname: 'Иван',
    lastname: 'Иванов',
  };

  test('POST /authors — 201, пароль захеширован', async () => {
    const res = await request(app).post('/api/v1.0/authors').send(validAuthor);

    expect(res.status).toBe(201);
    expect(res.body.id).toBe(1);
    expect(res.body.login).toBe('test@example.com');
    expect(res.body.password).toMatch(/^\$2b\$10\$/);
    expect(res.body.password).not.toBe('password123');
  });

  test('POST /authors — 400 при коротком пароле', async () => {
    const res = await request(app)
      .post('/api/v1.0/authors')
      .send({ ...validAuthor, password: '123' });

    expect(res.status).toBe(400);
    expect(res.body.errorCode).toBe('40001');
  });

  test('POST /authors — 409 при дубликате логина', async () => {
    await request(app).post('/api/v1.0/authors').send(validAuthor);
    const res = await request(app).post('/api/v1.0/authors').send(validAuthor);

    expect(res.status).toBe(409);
    expect(res.body.errorCode).toBe('40901');
  });

  test('GET /authors — 200, список', async () => {
    await request(app).post('/api/v1.0/authors').send(validAuthor);
    const res = await request(app).get('/api/v1.0/authors');

    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(1);
    expect(res.body[0].login).toBe('test@example.com');
  });

  test('GET /authors/:id — 404 для несуществующего', async () => {
    const res = await request(app).get('/api/v1.0/authors/999');

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40401');
  });

  test('PUT /authors/:id — 200, новый пароль захеширован', async () => {
    await request(app).post('/api/v1.0/authors').send(validAuthor);
    const res = await request(app)
      .put('/api/v1.0/authors/1')
      .send({
        ...validAuthor,
        login: 'new@example.com',
        password: 'newpassword123',
      });

    expect(res.status).toBe(200);
    expect(res.body.login).toBe('new@example.com');
    expect(res.body.password).toMatch(/^\$2b\$10\$/);
  });

  test('PUT /authors/:id — 404 при обновлении несуществующего', async () => {
    const res = await request(app)
      .put('/api/v1.0/authors/999')
      .send(validAuthor);

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40401');
  });

  test('DELETE /authors/:id — 204, затем 404', async () => {
    await request(app).post('/api/v1.0/authors').send(validAuthor);
    const del = await request(app).delete('/api/v1.0/authors/1');
    expect(del.status).toBe(204);

    const get = await request(app).get('/api/v1.0/authors/1');
    expect(get.status).toBe(404);
  });

  test('DELETE /authors/:id — 404 при удалении несуществующего', async () => {
    const res = await request(app).delete('/api/v1.0/authors/999');

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40401');
  });

  test('GET /authors/by-topic/:topicId — возвращает автора темы', async () => {
    await request(app).post('/api/v1.0/authors').send(validAuthor);
    await request(app).post('/api/v1.0/topics').send({
      authorId: 1,
      title: 'Test Topic',
      content: 'Content',
    });

    const res = await request(app).get('/api/v1.0/authors/by-topic/1');

    expect(res.status).toBe(200);
    expect(res.body.id).toBe(1);
    expect(res.body.login).toBe('test@example.com');
  });

  test('GET /authors/by-topic/:topicId — 404, если темы нет', async () => {
    const res = await request(app).get('/api/v1.0/authors/by-topic/999');

    expect(res.status).toBe(404);
    expect(res.body.errorCode).toBe('40402');
  });
});

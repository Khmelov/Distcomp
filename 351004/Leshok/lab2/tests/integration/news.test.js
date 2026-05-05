const request = require('supertest');
const { GenericContainer, Wait } = require('testcontainers');
const { Sequelize } = require('sequelize');

let container;
let app;

beforeAll(async () => {
  // Запускаем контейнер PostgreSQL
  container = await new GenericContainer('postgres:15')
    .withEnvironment({ POSTGRES_USER: 'postgres', POSTGRES_PASSWORD: 'postgres', POSTGRES_DB: 'testdb' })
    .withExposedPorts(5432)
    .withWaitStrategy(Wait.forLogMessage('database system is ready to accept connections'))
    .start();

  // Переопределяем переменные окружения для приложения
  process.env.DB_HOST = container.getHost();
  process.env.DB_PORT = container.getMappedPort(5432);
  process.env.DB_USER = 'postgres';
  process.env.DB_PASSWORD = 'postgres';
  process.env.DB_NAME = 'testdb';
  process.env.NODE_ENV = 'test';

  // Принудительно перезагружаем модули, чтобы app заново сконфигурировался
  jest.resetModules();
  app = require('../../src/app');

  // Синхронизируем модели (создаём таблицы)
  const { sequelize, Creator, News, Sticker, Note, NewsSticker } = require('../../src/models');
  await sequelize.sync({ force: true });
});

afterAll(async () => {
  const { sequelize } = require('../../src/models');
  await sequelize.close();
  await container.stop();
});

describe('News API', () => {
  let testCreatorId;

  beforeAll(async () => {
    // Создаём одного создателя для всех тестов
    const res = await request(app)
      .post('/api/v1.0/creators')
      .send({ login: 'news_test_user', password: 'testpass123', firstname: 'Test', lastname: 'User' });
    testCreatorId = res.body.id;
  });

  test('POST /api/v1.0/news should create news', async () => {
    const res = await request(app)
      .post('/api/v1.0/news')
      .send({ title: 'Test News', content: 'Content', creatorId: testCreatorId });
    expect(res.statusCode).toBe(201);
    expect(res.body).toHaveProperty('id');
    expect(res.body.title).toBe('Test News');
  });

  test('GET /api/v1.0/news/:id should return news', async () => {
    const createRes = await request(app)
      .post('/api/v1.0/news')
      .send({ title: 'Sample', content: 'Text', creatorId: testCreatorId });
    const id = createRes.body.id;
    const getRes = await request(app).get(`/api/v1.0/news/${id}`);
    expect(getRes.statusCode).toBe(200);
    expect(getRes.body.title).toBe('Sample');
  });

  test('PUT /api/v1.0/news/:id should update news', async () => {
    const createRes = await request(app)
      .post('/api/v1.0/news')
      .send({ title: 'Old', content: 'Old content', creatorId: testCreatorId });
    const id = createRes.body.id;
    const updateRes = await request(app)
      .put(`/api/v1.0/news/${id}`)
      .send({ title: 'New Title', content: 'New content', creatorId: testCreatorId });
    expect(updateRes.statusCode).toBe(200);
    expect(updateRes.body.title).toBe('New Title');
  });

  test('DELETE /api/v1.0/news/:id should delete news', async () => {
    const createRes = await request(app)
      .post('/api/v1.0/news')
      .send({ title: 'ToDelete', content: 'Content', creatorId: testCreatorId });
    const id = createRes.body.id;
    const delRes = await request(app).delete(`/api/v1.0/news/${id}`);
    expect(delRes.statusCode).toBe(204);
    const getRes = await request(app).get(`/api/v1.0/news/${id}`);
    expect(getRes.statusCode).toBe(404);
  });

  test('PUT /api/v1.0/news/:id with invalid creatorId should return 404', async () => {
    const createRes = await request(app)
      .post('/api/v1.0/news')
      .send({ title: 'Temp', content: 'Temp', creatorId: testCreatorId });
    const id = createRes.body.id;
    const updateRes = await request(app)
      .put(`/api/v1.0/news/${id}`)
      .send({ title: 'Updated', content: 'Updated', creatorId: 99999 });
    expect(updateRes.statusCode).toBe(404);
    expect(updateRes.body.errorCode).toBe('40402');
  });

  // Исправленный тест на длинный заголовок (более 128 символов)
  test('POST /api/v1.0/news with too long title should return 4xx', async () => {
    const longTitle = 'a'.repeat(150); // 150 символов > 128
    const res = await request(app)
      .post('/api/v1.0/news')
      .send({ title: longTitle, content: 'content', creatorId: testCreatorId });
    expect(res.statusCode).toBeGreaterThanOrEqual(400);
    expect(res.statusCode).toBeLessThan(500);
  });

  // Исправленный тест на дубликат заголовка
  test('POST /api/v1.0/news with duplicate title should return 403', async () => {
    const uniqueTitle = `DuplicateTest_${Date.now()}`;
    // Первое создание – успех
    await request(app)
      .post('/api/v1.0/news')
      .send({ title: uniqueTitle, content: 'First', creatorId: testCreatorId });
    // Второе с тем же заголовком – должно упасть на уникальность
    const duplicateRes = await request(app)
      .post('/api/v1.0/news')
      .send({ title: uniqueTitle, content: 'Second', creatorId: testCreatorId });
    expect(duplicateRes.statusCode).toBe(403);
    expect(duplicateRes.body.errorCode).toBe('40301');
  });
});
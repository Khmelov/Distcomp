const request = require('supertest');
const { GenericContainer, Wait } = require('testcontainers');

let container;
let app;

beforeAll(async () => {
  container = await new GenericContainer('postgres:15')
    .withEnvironment({ POSTGRES_USER: 'postgres', POSTGRES_PASSWORD: 'postgres', POSTGRES_DB: 'testdb' })
    .withExposedPorts(5432)
    .withWaitStrategy(Wait.forLogMessage('database system is ready to accept connections'))
    .start();

  process.env.DB_HOST = container.getHost();
  process.env.DB_PORT = container.getMappedPort(5432);
  process.env.DB_USER = 'postgres';
  process.env.DB_PASSWORD = 'postgres';
  process.env.DB_NAME = 'testdb';
  process.env.NODE_ENV = 'test';

  jest.resetModules();
  app = require('../../src/app');

  const { sequelize } = require('../../src/models');
  await sequelize.sync({ force: true });
});

afterAll(async () => {
  const { sequelize } = require('../../src/models');
  await sequelize.close();
  await container.stop();
});

describe('Creator API', () => {
  test('POST /api/v1.0/creators should create a creator', async () => {
    const res = await request(app)
      .post('/api/v1.0/creators')
      .send({ login: 'john_doe', password: 'secret', firstname: 'John', lastname: 'Doe' });
    expect(res.statusCode).toBe(201);
    expect(res.body).toHaveProperty('id');
    expect(res.body.login).toBe('john_doe');
  });

  test('GET /api/v1.0/creators/:id should return a creator', async () => {
    const createRes = await request(app)
      .post('/api/v1.0/creators')
      .send({ login: 'jane', password: 'pass', firstname: 'Jane', lastname: 'Smith' });
    const id = createRes.body.id;
    const getRes = await request(app).get(`/api/v1.0/creators/${id}`);
    expect(getRes.statusCode).toBe(200);
    expect(getRes.body.login).toBe('jane');
  });

  test('GET /api/v1.0/creators should support pagination', async () => {
    const res = await request(app).get('/api/v1.0/creators?page=1&limit=5&sort=id&order=ASC');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('data');
    expect(res.body).toHaveProperty('total');
  });

  test('PUT /api/v1.0/creators/:id should update a creator', async () => {
    const createRes = await request(app)
      .post('/api/v1.0/creators')
      .send({ login: 'updatable', password: 'old', firstname: 'Old', lastname: 'Name' });
    const id = createRes.body.id;
    const updateRes = await request(app)
      .put(`/api/v1.0/creators/${id}`)
      .send({ login: 'updated', password: 'new', firstname: 'New', lastname: 'Name' });
    expect(updateRes.statusCode).toBe(200);
    expect(updateRes.body.login).toBe('updated');
  });

  test('DELETE /api/v1.0/creators/:id should delete a creator', async () => {
    const createRes = await request(app)
      .post('/api/v1.0/creators')
      .send({ login: 'deleteMe', password: 'x', firstname: 'Del', lastname: 'Me' });
    const id = createRes.body.id;
    const delRes = await request(app).delete(`/api/v1.0/creators/${id}`);
    expect(delRes.statusCode).toBe(204);
    const getRes = await request(app).get(`/api/v1.0/creators/${id}`);
    expect(getRes.statusCode).toBe(404);
  });
});
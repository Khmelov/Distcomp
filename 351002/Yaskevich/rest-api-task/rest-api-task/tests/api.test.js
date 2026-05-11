process.env.STORAGE_MODE = 'memory';

const request = require('supertest');
const app = require('../src/server');

describe('REST API /api/v1.0', () => {
    it('supports CRUD for creator', async () => {
        const create = await request(app).post('/api/v1.0/creators').send({
            login: 'john',
            password: 'secret',
            firstname: 'John',
            lastname: 'Doe'
        });
        expect(create.statusCode).toBe(201);
        expect(create.body.id).toBeDefined();

        const getById = await request(app).get(`/api/v1.0/creators/${create.body.id}`);
        expect(getById.statusCode).toBe(200);
        expect(getById.body.login).toBe('john');

        const update = await request(app).put(`/api/v1.0/creators/${create.body.id}`).send({
            id: create.body.id,
            login: 'johnny',
            password: 'secret2',
            firstname: 'John',
            lastname: 'Snow'
        });
        expect(update.statusCode).toBe(200);
        expect(update.body.login).toBe('johnny');

        const remove = await request(app).delete(`/api/v1.0/creators/${create.body.id}`);
        expect(remove.statusCode).toBe(204);
    });

    it('supports CRUD for mark', async () => {
        const create = await request(app).post('/api/v1.0/marks').send({ name: 'urgent' });
        expect(create.statusCode).toBe(201);

        const list = await request(app).get('/api/v1.0/marks?page=1&size=10&sortBy=name&sortOrder=asc&q=urg');
        expect(list.statusCode).toBe(200);
        expect(Array.isArray(list.body.items)).toBe(true);

        const update = await request(app).put(`/api/v1.0/marks/${create.body.id}`).send({
            id: create.body.id,
            name: 'review'
        });
        expect(update.statusCode).toBe(200);

        const remove = await request(app).delete(`/api/v1.0/marks/${create.body.id}`);
        expect(remove.statusCode).toBe(204);
    });

    it('supports CRUD for topic and post with links', async () => {
        const creator = await request(app).post('/api/v1.0/creators').send({
            login: 'alice',
            password: 'secret',
            firstname: 'Alice',
            lastname: 'Cooper'
        });
        const mark1 = await request(app).post('/api/v1.0/marks').send({ name: 'java' });
        const mark2 = await request(app).post('/api/v1.0/marks').send({ name: 'postgres' });

        const topicCreate = await request(app).post('/api/v1.0/topics').send({
            title: 'Backend',
            creatorId: creator.body.id,
            markIds: [mark1.body.id, mark2.body.id]
        });
        expect(topicCreate.statusCode).toBe(201);
        expect(topicCreate.body.markIds).toHaveLength(2);

        const topicList = await request(app).get('/api/v1.0/topics?page=1&size=10&sortBy=title&sortOrder=desc');
        expect(topicList.statusCode).toBe(200);
        expect(topicList.body.total).toBeGreaterThanOrEqual(1);

        const postCreate = await request(app).post('/api/v1.0/posts').send({
            title: 'First post',
            content: 'Hello world',
            topicId: topicCreate.body.id
        });
        expect(postCreate.statusCode).toBe(201);

        const postUpdate = await request(app).put(`/api/v1.0/posts/${postCreate.body.id}`).send({
            id: postCreate.body.id,
            title: 'Updated post',
            content: 'Updated content',
            topicId: topicCreate.body.id
        });
        expect(postUpdate.statusCode).toBe(200);
        expect(postUpdate.body.title).toBe('Updated post');

        const postDelete = await request(app).delete(`/api/v1.0/posts/${postCreate.body.id}`);
        expect(postDelete.statusCode).toBe(204);

        const topicDelete = await request(app).delete(`/api/v1.0/topics/${topicCreate.body.id}`);
        expect(topicDelete.statusCode).toBe(204);
    });
});

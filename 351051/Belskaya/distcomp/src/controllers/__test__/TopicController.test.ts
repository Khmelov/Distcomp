import express, { NextFunction, Request, Response } from 'express';
import { request as httpRequest, Server } from 'http';
import { AddressInfo } from 'net';
import { topicService } from '../../services';
import topicRouter from './../TopicController';

jest.mock('../../services', () => ({
  topicService: {
    create: jest.fn(),
    findAll: jest.fn(),
    findByFilters: jest.fn(),
    findById: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  },
}));

const mockedTopicService = topicService as jest.Mocked<typeof topicService>;

describe('TopicController', () => {
  let server: Server;
  let baseUrl: string;

  const topic = {
    id: 1,
    authorId: 10,
    title: 'Test topic',
    content: 'Test content',
    created: '2026-01-01T00:00:00.000Z',
    modified: '2026-01-01T00:00:00.000Z',
  };

  const app = express();

  app.use(express.json());
  app.use('/topics', topicRouter);

  app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
    res.status(500).json({ message: err.message });
  });

  beforeAll(async () => {
    await new Promise<void>((resolve, reject) => {
      server = app.listen(0, () => {
        resolve();
      });

      server.on('error', reject);
    });

    const address = server.address() as AddressInfo;
    baseUrl = `http://127.0.0.1:${address.port}`;
  });

  afterAll(async () => {
    await new Promise<void>((resolve, reject) => {
      server.close((err) => {
        if (err) reject(err);
        else resolve();
      });
    });
  });

  beforeEach(() => {
    jest.clearAllMocks();

    mockedTopicService.create.mockResolvedValue(topic as any);
    mockedTopicService.findAll.mockResolvedValue([topic] as any);
    mockedTopicService.findByFilters.mockResolvedValue([topic] as any);
    mockedTopicService.findById.mockResolvedValue(topic as any);
    mockedTopicService.update.mockResolvedValue(topic as any);
    mockedTopicService.delete.mockResolvedValue(undefined);
  });

  async function makeRequest(
    method: string,
    path: string,
    body?: unknown
  ): Promise<{ status: number; body: any }> {
    return new Promise((resolve, reject) => {
      const headers: Record<string, string> = {};

      if (body !== undefined) {
        headers['Content-Type'] = 'application/json';
      }

      const req = httpRequest(
        `${baseUrl}${path}`,
        { method, headers },
        (res) => {
          const chunks: Buffer[] = [];

          res.on('data', (chunk: Buffer) => chunks.push(chunk));

          res.on('end', () => {
            const responseText = Buffer.concat(chunks).toString();

            resolve({
              status: res.statusCode!,
              body: responseText ? JSON.parse(responseText) : undefined,
            });
          });
        }
      );

      req.on('error', reject);

      if (body !== undefined) {
        req.write(JSON.stringify(body));
      }

      req.end();
    });
  }

  describe('POST /topics', () => {
    it('should create a topic and return status 201', async () => {
      const dto = {
        authorId: 10,
        title: 'Test topic',
        content: 'Test content',
        markers: ['JavaScript', 'TypeScript'],
      };

      const response = await makeRequest('POST', '/topics', dto);

      expect(response.status).toBe(201);
      expect(response.body).toEqual(topic);
      expect(mockedTopicService.create).toHaveBeenCalledWith(dto);
    });

    it('should forward errors to error handler', async () => {
      mockedTopicService.create.mockRejectedValue(new Error('Creation failed'));

      const response = await makeRequest('POST', '/topics', {});

      expect(response.status).toBe(500);
      expect(response.body).toEqual({
        message: 'Creation failed',
      });
    });
  });

  describe('GET /topics', () => {
    it('should return all topics with status 200', async () => {
      const response = await makeRequest('GET', '/topics');

      expect(response.status).toBe(200);
      expect(response.body).toEqual([topic]);
      expect(mockedTopicService.findAll).toHaveBeenCalledWith({
        page: undefined,
        size: undefined,
        sortBy: undefined,
        sortOrder: 'asc',
      });
    });

    it('should pass pagination parameters to service', async () => {
      const response = await makeRequest(
        'GET',
        '/topics?page=2&size=5&sortBy=title&sortOrder=desc'
      );

      expect(response.status).toBe(200);
      expect(mockedTopicService.findAll).toHaveBeenCalledWith({
        page: 2,
        size: 5,
        sortBy: 'title',
        sortOrder: 'desc',
      });
    });
  });

  describe('GET /topics/by-filters', () => {
    it('should return topics filtered by query parameters', async () => {
      const response = await makeRequest(
        'GET',
        '/topics/by-filters?markerNames=JavaScript,TypeScript&markerIds=1,2&authorLogin=john&title=Test&content=hello'
      );

      expect(response.status).toBe(200);
      expect(response.body).toEqual([topic]);

      expect(mockedTopicService.findByFilters).toHaveBeenCalledWith({
        markerNames: ['JavaScript', 'TypeScript'],
        markerIds: [1, 2],
        authorLogin: 'john',
        title: 'Test',
        content: 'hello',
      });
    });

    it('should pass undefined filters when query parameters are absent', async () => {
      const response = await makeRequest('GET', '/topics/by-filters');

      expect(response.status).toBe(200);
      expect(mockedTopicService.findByFilters).toHaveBeenCalledWith({
        markerNames: undefined,
        markerIds: undefined,
        authorLogin: undefined,
        title: undefined,
        content: undefined,
      });
    });

    it('should forward errors to error handler', async () => {
      mockedTopicService.findByFilters.mockRejectedValue(
        new Error('Filtering failed')
      );

      const response = await makeRequest('GET', '/topics/by-filters');

      expect(response.status).toBe(500);
      expect(response.body).toEqual({
        message: 'Filtering failed',
      });
    });
  });

  describe('GET /topics/:id', () => {
    it('should return topic by id', async () => {
      const response = await makeRequest('GET', '/topics/1');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(topic);
      expect(mockedTopicService.findById).toHaveBeenCalledWith(1);
    });
  });

  describe('PUT /topics/:id', () => {
    it('should update topic and return status 200', async () => {
      const dto = {
        authorId: 10,
        title: 'Updated topic',
        content: 'Updated content',
      };

      const response = await makeRequest('PUT', '/topics/1', dto);

      expect(response.status).toBe(200);
      expect(response.body).toEqual(topic);
      expect(mockedTopicService.update).toHaveBeenCalledWith(1, dto);
    });
  });

  describe('DELETE /topics/:id', () => {
    it('should delete topic and return status 204', async () => {
      const response = await makeRequest('DELETE', '/topics/1');

      expect(response.status).toBe(204);
      expect(response.body).toBeUndefined();
      expect(mockedTopicService.delete).toHaveBeenCalledWith(1);
    });
  });
});

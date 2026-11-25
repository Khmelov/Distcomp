import express, { NextFunction, Request, Response } from 'express';
import { request as httpRequest, Server } from 'http';
import { AddressInfo } from 'net';
import { messageService } from '../../services';
import messageRouter from './../MessageController';

jest.mock('../../services', () => ({
  messageService: {
    create: jest.fn(),
    findAll: jest.fn(),
    findByTopicId: jest.fn(),
    findById: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  },
}));

const mockedMessageService = messageService as jest.Mocked<
  typeof messageService
>;

describe('MessageController', () => {
  let server: Server;
  let baseUrl: string;

  const message = {
    id: 1,
    topicId: 10,
    content: 'Test message',
  };

  const app = express();

  app.use(express.json());
  app.use('/messages', messageRouter);

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

    mockedMessageService.create.mockResolvedValue(message as any);
    mockedMessageService.findAll.mockResolvedValue([message] as any);
    mockedMessageService.findByTopicId.mockResolvedValue([message] as any);
    mockedMessageService.findById.mockResolvedValue(message as any);
    mockedMessageService.update.mockResolvedValue(message as any);
    mockedMessageService.delete.mockResolvedValue(undefined);
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

  describe('POST /messages', () => {
    it('should create a message and return status 201', async () => {
      const dto = {
        topicId: 10,
        content: 'Test message',
      };

      const response = await makeRequest('POST', '/messages', dto);

      expect(response.status).toBe(201);
      expect(response.body).toEqual(message);
      expect(mockedMessageService.create).toHaveBeenCalledWith(dto);
    });

    it('should forward errors to error handler', async () => {
      mockedMessageService.create.mockRejectedValue(
        new Error('Creation failed')
      );

      const response = await makeRequest('POST', '/messages', {});

      expect(response.status).toBe(500);
      expect(response.body).toEqual({
        message: 'Creation failed',
      });
    });
  });

  describe('GET /messages', () => {
    it('should return all messages with status 200', async () => {
      const response = await makeRequest('GET', '/messages');

      expect(response.status).toBe(200);
      expect(response.body).toEqual([message]);
      expect(mockedMessageService.findAll).toHaveBeenCalledWith({
        page: undefined,
        size: undefined,
        sortBy: undefined,
        sortOrder: 'asc',
      });
    });

    it('should pass pagination parameters to service', async () => {
      const response = await makeRequest(
        'GET',
        '/messages?page=2&size=5&sortBy=id&sortOrder=desc'
      );

      expect(response.status).toBe(200);
      expect(mockedMessageService.findAll).toHaveBeenCalledWith({
        page: 2,
        size: 5,
        sortBy: 'id',
        sortOrder: 'desc',
      });
    });
  });

  describe('GET /messages/by-topic/:topicId', () => {
    it('should return messages belonging to a topic', async () => {
      const response = await makeRequest('GET', '/messages/by-topic/10');

      expect(response.status).toBe(200);
      expect(response.body).toEqual([message]);
      expect(mockedMessageService.findByTopicId).toHaveBeenCalledWith(10);
    });
  });

  describe('GET /messages/:id', () => {
    it('should return message by id', async () => {
      const response = await makeRequest('GET', '/messages/1');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(message);
      expect(mockedMessageService.findById).toHaveBeenCalledWith(1);
    });
  });

  describe('PUT /messages/:id', () => {
    it('should update message and return status 200', async () => {
      const dto = {
        topicId: 10,
        content: 'Updated message',
      };

      const response = await makeRequest('PUT', '/messages/1', dto);

      expect(response.status).toBe(200);
      expect(response.body).toEqual(message);
      expect(mockedMessageService.update).toHaveBeenCalledWith(1, dto);
    });
  });

  describe('DELETE /messages/:id', () => {
    it('should delete message and return status 204', async () => {
      const response = await makeRequest('DELETE', '/messages/1');

      expect(response.status).toBe(204);
      expect(response.body).toBeUndefined();
      expect(mockedMessageService.delete).toHaveBeenCalledWith(1);
    });
  });
});

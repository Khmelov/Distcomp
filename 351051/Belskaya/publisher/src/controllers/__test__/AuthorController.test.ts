import express, { NextFunction, Request, Response } from 'express';
import { request as httpRequest, Server } from 'http';
import { AddressInfo } from 'net';
import { authorService } from '../../services';
import authorRouter from './../AuthorController';

jest.mock('../../services', () => ({
  authorService: {
    create: jest.fn(),
    findAll: jest.fn(),
    findByTopicId: jest.fn(),
    findById: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  },
}));

const mockedAuthorService = authorService as jest.Mocked<typeof authorService>;

describe('AuthorController', () => {
  let server: Server;
  let baseUrl: string;

  const author = {
    id: 1,
    login: 'john',
    firstname: 'John',
    lastname: 'Doe',
  };

  const app = express();

  app.use(express.json());
  app.use('/authors', authorRouter);

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

    mockedAuthorService.create.mockResolvedValue(author as any);
    mockedAuthorService.findAll.mockResolvedValue([author] as any);
    mockedAuthorService.findByTopicId.mockResolvedValue(author as any);
    mockedAuthorService.findById.mockResolvedValue(author as any);
    mockedAuthorService.update.mockResolvedValue(author as any);
    mockedAuthorService.delete.mockResolvedValue(undefined);
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
        {
          method,
          headers,
        },
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

  describe('POST /authors', () => {
    it('should create an author and return status 201', async () => {
      const dto = {
        login: 'john',
        password: 'password123',
        firstname: 'John',
        lastname: 'Doe',
      };

      const response = await makeRequest('POST', '/authors', dto);

      expect(response.status).toBe(201);
      expect(response.body).toEqual(author);
      expect(mockedAuthorService.create).toHaveBeenCalledWith(dto);
    });

    it('should forward errors to error handler', async () => {
      mockedAuthorService.create.mockRejectedValue(
        new Error('Creation failed')
      );

      const response = await makeRequest('POST', '/authors', {});

      expect(response.status).toBe(500);
      expect(response.body).toEqual({
        message: 'Creation failed',
      });
    });
  });

  describe('GET /authors', () => {
    it('should return all authors with status 200', async () => {
      const response = await makeRequest('GET', '/authors');

      expect(response.status).toBe(200);
      expect(response.body).toEqual([author]);
      expect(mockedAuthorService.findAll).toHaveBeenCalledWith({
        page: undefined,
        size: undefined,
        sortBy: undefined,
        sortOrder: 'asc',
      });
    });

    it('should pass pagination parameters to service', async () => {
      const response = await makeRequest(
        'GET',
        '/authors?page=2&size=5&sortBy=login&sortOrder=desc'
      );

      expect(response.status).toBe(200);
      expect(mockedAuthorService.findAll).toHaveBeenCalledWith({
        page: 2,
        size: 5,
        sortBy: 'login',
        sortOrder: 'desc',
      });
    });
  });

  describe('GET /authors/by-topic/:topicId', () => {
    it('should return author by topic id', async () => {
      const response = await makeRequest('GET', '/authors/by-topic/10');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(author);
      expect(mockedAuthorService.findByTopicId).toHaveBeenCalledWith(10);
    });
  });

  describe('GET /authors/:id', () => {
    it('should return author by id', async () => {
      const response = await makeRequest('GET', '/authors/1');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(author);
      expect(mockedAuthorService.findById).toHaveBeenCalledWith(1);
    });
  });

  describe('PUT /authors/:id', () => {
    it('should update author and return status 200', async () => {
      const dto = {
        login: 'john',
        password: 'new-password',
        firstname: 'John',
        lastname: 'Smith',
      };

      const response = await makeRequest('PUT', '/authors/1', dto);

      expect(response.status).toBe(200);
      expect(response.body).toEqual(author);
      expect(mockedAuthorService.update).toHaveBeenCalledWith(1, dto);
    });
  });

  describe('DELETE /authors/:id', () => {
    it('should delete author and return status 204', async () => {
      const response = await makeRequest('DELETE', '/authors/1');

      expect(response.status).toBe(204);
      expect(response.body).toBeUndefined();
      expect(mockedAuthorService.delete).toHaveBeenCalledWith(1);
    });
  });
});

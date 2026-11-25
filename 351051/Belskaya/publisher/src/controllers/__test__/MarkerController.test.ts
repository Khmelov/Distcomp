import express, { NextFunction, Request, Response } from 'express';
import { request as httpRequest, Server } from 'http';
import { AddressInfo } from 'net';
import { markerService } from '../../services';
import markerRouter from './../MarkerController';

jest.mock('../../services', () => ({
  markerService: {
    create: jest.fn(),
    findAll: jest.fn(),
    findByTopicId: jest.fn(),
    findById: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  },
}));

const mockedMarkerService = markerService as jest.Mocked<typeof markerService>;

describe('MarkerController', () => {
  let server: Server;
  let baseUrl: string;

  const marker = {
    id: 1,
    name: 'JavaScript',
  };

  const app = express();

  app.use(express.json());
  app.use('/markers', markerRouter);

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

    mockedMarkerService.create.mockResolvedValue(marker as any);
    mockedMarkerService.findAll.mockResolvedValue([marker] as any);
    mockedMarkerService.findByTopicId.mockResolvedValue([marker] as any);
    mockedMarkerService.findById.mockResolvedValue(marker as any);
    mockedMarkerService.update.mockResolvedValue(marker as any);
    mockedMarkerService.delete.mockResolvedValue(undefined);
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

  describe('POST /markers', () => {
    it('should create a marker and return status 201', async () => {
      const dto = { name: 'JavaScript' };

      const response = await makeRequest('POST', '/markers', dto);

      expect(response.status).toBe(201);
      expect(response.body).toEqual(marker);
      expect(mockedMarkerService.create).toHaveBeenCalledWith(dto);
    });

    it('should forward errors to error handler', async () => {
      mockedMarkerService.create.mockRejectedValue(
        new Error('Creation failed')
      );

      const response = await makeRequest('POST', '/markers', {});

      expect(response.status).toBe(500);
      expect(response.body).toEqual({
        message: 'Creation failed',
      });
    });
  });

  describe('GET /markers', () => {
    it('should return all markers with status 200', async () => {
      const response = await makeRequest('GET', '/markers');

      expect(response.status).toBe(200);
      expect(response.body).toEqual([marker]);
      expect(mockedMarkerService.findAll).toHaveBeenCalledWith({
        page: undefined,
        size: undefined,
        sortBy: undefined,
        sortOrder: 'asc',
      });
    });

    it('should pass pagination parameters to service', async () => {
      const response = await makeRequest(
        'GET',
        '/markers?page=2&size=5&sortBy=name&sortOrder=desc'
      );

      expect(response.status).toBe(200);
      expect(mockedMarkerService.findAll).toHaveBeenCalledWith({
        page: 2,
        size: 5,
        sortBy: 'name',
        sortOrder: 'desc',
      });
    });
  });

  describe('GET /markers/by-topic/:topicId', () => {
    it('should return markers belonging to a topic', async () => {
      const response = await makeRequest('GET', '/markers/by-topic/10');

      expect(response.status).toBe(200);
      expect(response.body).toEqual([marker]);
      expect(mockedMarkerService.findByTopicId).toHaveBeenCalledWith(10);
    });
  });

  describe('GET /markers/:id', () => {
    it('should return marker by id', async () => {
      const response = await makeRequest('GET', '/markers/1');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(marker);
      expect(mockedMarkerService.findById).toHaveBeenCalledWith(1);
    });
  });

  describe('PUT /markers/:id', () => {
    it('should update marker and return status 200', async () => {
      const dto = { name: 'TypeScript' };

      const response = await makeRequest('PUT', '/markers/1', dto);

      expect(response.status).toBe(200);
      expect(response.body).toEqual(marker);
      expect(mockedMarkerService.update).toHaveBeenCalledWith(1, dto);
    });
  });

  describe('DELETE /markers/:id', () => {
    it('should delete marker and return status 204', async () => {
      const response = await makeRequest('DELETE', '/markers/1');

      expect(response.status).toBe(204);
      expect(response.body).toBeUndefined();
      expect(mockedMarkerService.delete).toHaveBeenCalledWith(1);
    });
  });
});

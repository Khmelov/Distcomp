import { RedisService } from './redis.service';

// Mock ioredis
jest.mock('ioredis', () => {
  const store = new Map<string, { value: string; expiry?: number }>();

  return jest.fn().mockImplementation(() => ({
    connect: jest.fn().mockResolvedValue(undefined),
    quit: jest.fn().mockResolvedValue(undefined),
    get: jest.fn().mockImplementation((key: string) => {
      const entry = store.get(key);
      return Promise.resolve(entry ? entry.value : null);
    }),
    set: jest.fn().mockImplementation((key: string, value: string) => {
      store.set(key, { value });
      return Promise.resolve('OK');
    }),
    del: jest.fn().mockImplementation((...keys: string[]) => {
      let count = 0;
      for (const key of keys) {
        if (store.delete(key)) count++;
      }
      return Promise.resolve(count);
    }),
    keys: jest.fn().mockImplementation((pattern: string) => {
      const prefix = pattern.replace('*', '');
      const matched = [...store.keys()].filter((k) => k.startsWith(prefix));
      return Promise.resolve(matched);
    }),
    _store: store,
  }));
});

describe('RedisService', () => {
  let service: RedisService;
  let store: Map<string, { value: string; expiry?: number }>;

  beforeEach(() => {
    service = new RedisService();
    // Access the mock store via the ioredis constructor
    const Redis = require('ioredis');
    const instance = Redis.mock.results[Redis.mock.results.length - 1].value;
    store = instance._store;
    store.clear();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('get', () => {
    it('should return null for missing key', async () => {
      const result = await service.get('nonexistent');
      expect(result).toBeNull();
    });

    it('should return parsed value for existing key', async () => {
      store.set('test-key', { value: JSON.stringify({ id: 1, name: 'test' }) });

      const result = await service.get<{ id: number; name: string }>('test-key');
      expect(result).toEqual({ id: 1, name: 'test' });
    });
  });

  describe('set', () => {
    it('should store a value', async () => {
      await service.set('my-key', { hello: 'world' });

      expect(store.has('my-key')).toBe(true);
      expect(JSON.parse(store.get('my-key')!.value)).toEqual({ hello: 'world' });
    });
  });

  describe('del', () => {
    it('should delete keys', async () => {
      store.set('a', { value: '1' });
      store.set('b', { value: '2' });

      await service.del('a', 'b');

      expect(store.has('a')).toBe(false);
      expect(store.has('b')).toBe(false);
    });

    it('should do nothing when called with no keys', async () => {
      await expect(service.del()).resolves.toBeUndefined();
    });
  });

  describe('delByPattern', () => {
    it('should delete keys matching pattern', async () => {
      store.set('user:1', { value: '1' });
      store.set('user:2', { value: '2' });
      store.set('article:1', { value: '3' });

      await service.delByPattern('user:*');

      expect(store.has('user:1')).toBe(false);
      expect(store.has('user:2')).toBe(false);
      expect(store.has('article:1')).toBe(true);
    });
  });

  describe('onModuleDestroy', () => {
    it('should call quit on the redis client', async () => {
      await expect(service.onModuleDestroy()).resolves.toBeUndefined();
    });
  });
});

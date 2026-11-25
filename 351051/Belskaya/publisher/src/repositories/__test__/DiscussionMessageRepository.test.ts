import { Message } from '../../entities';
import {
  AppError,
  ConflictError,
  NotFoundError,
  UnprocessableEntityError,
  ValidationError,
} from '../../errors/AppError';
import { DiscussionMessageRepository } from '../DiscussionMessageRepository';

jest.mock('../../db', () => ({
  redisClient: {
    get: jest.fn(),
    set: jest.fn(),
    del: jest.fn(),
    keys: jest.fn().mockResolvedValue([]),
  },
}));

jest.mock('../../kafka/requestReply', () => ({
  sendKafkaRequest: jest.fn(),
}));

import { sendKafkaRequest } from '../../kafka/requestReply';
import { redisClient } from '../../db';

const sendKafkaRequestMock = sendKafkaRequest as jest.MockedFunction<
  typeof sendKafkaRequest
>;

const redisGet = redisClient.get as jest.Mock;
const redisSet = redisClient.set as jest.Mock;
const redisDel = redisClient.del as jest.Mock;
const redisKeys = redisClient.keys as jest.Mock;

describe('DiscussionMessageRepository', () => {
  let repository: DiscussionMessageRepository;

  const message: Message = {
    id: 100,
    topicId: 10,
    content: 'Test message',
    state: 'APPROVE',
  };

  beforeEach(() => {
    repository = new DiscussionMessageRepository();
    jest.clearAllMocks();
    sendKafkaRequestMock.mockReset();
    redisGet.mockResolvedValue(null);
    redisKeys.mockResolvedValue([]);
  });

  describe('findAll', () => {
    it('should return all messages', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_ALL_RESULT',
        correlationId: 'x',
        data: [message],
      });

      const result = await repository.findAll();

      expect(result).toEqual([message]);
      expect(sendKafkaRequestMock).toHaveBeenCalledWith('GET_ALL', {});
      expect(redisSet).toHaveBeenCalled();
    });

    it('should return cached messages if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify([message]));

      const result = await repository.findAll();

      expect(result).toEqual([message]);
      expect(sendKafkaRequestMock).not.toHaveBeenCalled();
    });

    it('should throw ValidationError on 400', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_ALL_ERROR',
        correlationId: 'x',
        error: '400 bad request',
      });

      await expect(repository.findAll()).rejects.toBeInstanceOf(
        ValidationError
      );
    });

    it('should throw ConflictError on 403', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_ALL_ERROR',
        correlationId: 'x',
        error: '403 forbidden',
      });

      await expect(repository.findAll()).rejects.toBeInstanceOf(ConflictError);
    });

    it('should throw NotFoundError on 404', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_ALL_ERROR',
        correlationId: 'x',
        error: '404 not found',
      });

      await expect(repository.findAll()).rejects.toBeInstanceOf(NotFoundError);
    });

    it('should throw UnprocessableEntityError on 422', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_ALL_ERROR',
        correlationId: 'x',
        error: '422 unprocessable',
      });

      await expect(repository.findAll()).rejects.toBeInstanceOf(
        UnprocessableEntityError
      );
    });

    it('should throw AppError on timeout', async () => {
      sendKafkaRequestMock.mockRejectedValue(
        new Error('Kafka timeout for action GET_ALL')
      );

      await expect(repository.findAll()).rejects.toMatchObject({
        statusCode: 504,
        errorCode: '50401',
      });
    });
  });

  describe('findById', () => {
    it('should return a message by id', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_BY_ID_RESULT',
        correlationId: 'x',
        data: message,
      });

      const result = await repository.findById(100);

      expect(result).toEqual(message);
      expect(sendKafkaRequestMock).toHaveBeenCalledWith('GET_BY_ID', {
        id: 100,
      });
      expect(redisSet).toHaveBeenCalled();
    });

    it('should return cached message if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify(message));

      const result = await repository.findById(100);

      expect(result).toEqual(message);
      expect(sendKafkaRequestMock).not.toHaveBeenCalled();
    });

    it('should return undefined if message does not exist', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_BY_ID_RESULT',
        correlationId: 'x',
        data: null,
      });

      const result = await repository.findById(100);

      expect(result).toBeUndefined();
    });
  });

  describe('findByTopicId', () => {
    it('should return messages by topic id', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_BY_TOPIC_RESULT',
        correlationId: 'x',
        data: [message],
      });

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([message]);
      expect(sendKafkaRequestMock).toHaveBeenCalledWith('GET_BY_TOPIC', {
        topicId: 10,
        sortOrder: 'asc',
      });
    });

    it('should pass sortOrder to Discussion', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_BY_TOPIC_RESULT',
        correlationId: 'x',
        data: [message],
      });

      await repository.findByTopicId(10, 'desc');

      expect(sendKafkaRequestMock).toHaveBeenCalledWith('GET_BY_TOPIC', {
        topicId: 10,
        sortOrder: 'desc',
      });
    });
  });

  describe('save', () => {
    it('should create a message', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'CREATE_RESULT',
        correlationId: 'x',
        data: message,
      });

      const { id, ...newMessage } = message;
      const result = await repository.save(newMessage);

      expect(result).toEqual(message);
      expect(redisSet).toHaveBeenCalledWith(
        `message:${message.id}`,
        JSON.stringify(message),
        expect.any(Object)
      );
    });
  });

  describe('update', () => {
    it('should update a message', async () => {
      const updatedMessage = { ...message, content: 'Updated message' };

      sendKafkaRequestMock.mockResolvedValue({
        action: 'UPDATE_RESULT',
        correlationId: 'x',
        data: updatedMessage,
      });

      const result = await repository.update(100, {
        content: 'Updated message',
      });

      expect(result).toEqual(updatedMessage);
      expect(redisDel).toHaveBeenCalledWith(`message:${message.id}`);
    });

    it('should return undefined if message does not exist', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'UPDATE_RESULT',
        correlationId: 'x',
        data: null,
      });

      const result = await repository.update(100, {
        content: 'Updated message',
      });

      expect(result).toBeUndefined();
    });
  });

  describe('delete', () => {
    it('should delete a message', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'DELETE_RESULT',
        correlationId: 'x',
        data: true,
      });

      const result = await repository.delete(100);

      expect(result).toBe(true);
      expect(redisDel).toHaveBeenCalledWith(`message:${message.id}`);
    });

    it('should return false if message does not exist', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'DELETE_RESULT',
        correlationId: 'x',
        data: false,
      });

      const result = await repository.delete(100);

      expect(result).toBe(false);
    });
  });

  describe('existsById', () => {
    it('should return true if message exists', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_BY_ID_RESULT',
        correlationId: 'x',
        data: message,
      });

      const result = await repository.existsById(100);

      expect(result).toBe(true);
    });

    it('should return false if message does not exist', async () => {
      sendKafkaRequestMock.mockResolvedValue({
        action: 'GET_BY_ID_RESULT',
        correlationId: 'x',
        data: null,
      });

      const result = await repository.existsById(100);

      expect(result).toBe(false);
    });
  });
});

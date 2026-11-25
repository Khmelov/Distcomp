import { Message } from '../../entities';
import {
  AppError,
  ConflictError,
  NotFoundError,
  UnprocessableEntityError,
  ValidationError,
} from '../../errors/AppError';
import { DiscussionMessageRepository } from '../DiscussionMessageRepository';

jest.mock('../../kafka/requestReply', () => ({
  sendKafkaRequest: jest.fn(),
}));

import { sendKafkaRequest } from '../../kafka/requestReply';

const sendKafkaRequestMock = sendKafkaRequest as jest.MockedFunction<
  typeof sendKafkaRequest
>;

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
    sendKafkaRequestMock.mockReset();
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

    it('should throw AppError on unexpected error', async () => {
      sendKafkaRequestMock.mockRejectedValue(new Error('Something broke'));

      await expect(repository.findAll()).rejects.toBeInstanceOf(AppError);
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

    it('should throw AppError on server error', async () => {
      sendKafkaRequestMock.mockRejectedValue(
        new Error('Kafka timeout for action GET_BY_ID')
      );

      await expect(repository.findById(100)).rejects.toBeInstanceOf(AppError);
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

    it('should throw AppError on failed request', async () => {
      sendKafkaRequestMock.mockRejectedValue(
        new Error('Kafka timeout for action GET_BY_TOPIC')
      );

      await expect(repository.findByTopicId(10)).rejects.toBeInstanceOf(
        AppError
      );
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
      expect(sendKafkaRequestMock).toHaveBeenCalledWith(
        'CREATE',
        expect.objectContaining({
          topicId: 10,
          content: 'Test message',
          state: 'PENDING',
        })
      );
    });

    it('should throw AppError on failed request', async () => {
      sendKafkaRequestMock.mockRejectedValue(
        new Error('Kafka timeout for action CREATE')
      );

      await expect(
        repository.save({
          topicId: 10,
          content: 'Test message',
        })
      ).rejects.toBeInstanceOf(AppError);
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
      expect(sendKafkaRequestMock).toHaveBeenCalledWith('UPDATE', {
        id: 100,
        patch: { content: 'Updated message' },
      });
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

    it('should throw AppError on server error', async () => {
      sendKafkaRequestMock.mockRejectedValue(
        new Error('Kafka timeout for action UPDATE')
      );

      await expect(
        repository.update(100, { content: 'Updated message' })
      ).rejects.toBeInstanceOf(AppError);
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
      expect(sendKafkaRequestMock).toHaveBeenCalledWith('DELETE', {
        id: 100,
      });
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

    it('should throw AppError on server error', async () => {
      sendKafkaRequestMock.mockRejectedValue(
        new Error('Kafka timeout for action DELETE')
      );

      await expect(repository.delete(100)).rejects.toBeInstanceOf(AppError);
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

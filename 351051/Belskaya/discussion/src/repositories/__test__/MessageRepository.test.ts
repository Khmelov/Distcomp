jest.mock("../../db/cassandra", () => ({
  cassandraClient: {
    batch: jest.fn(),
    execute: jest.fn(),
  },
}));

import { cassandraClient } from "../../db/cassandra";
import { MessageRepository } from "../MessageRepository";

const batchMock = cassandraClient.batch as jest.Mock;
const executeMock = cassandraClient.execute as jest.Mock;

describe("MessageRepository", () => {
  let repository: MessageRepository;

  const message = {
    id: 100,
    topicId: 10,
    country: "US",
    content: "Test message",
  };

  const cassandraRow = {
    id: BigInt(100),
    topic_id: BigInt(10),
    country: "US",
    content: "Test message",
  };

  beforeEach(() => {
    jest.clearAllMocks();
    repository = new MessageRepository();
  });

  describe("save", () => {
    it("should save a message into both tables", async () => {
      batchMock.mockResolvedValue(undefined);

      await repository.save(message);

      expect(batchMock).toHaveBeenCalledTimes(1);

      const [queries, options] = batchMock.mock.calls[0];

      expect(queries).toHaveLength(2);
      expect(queries[0].query).toContain("distcomp.tbl_message");
      expect(queries[1].query).toContain("distcomp.tbl_message_by_topic");
      expect(options).toEqual({ prepare: true });
    });

    it("should save null when country is not provided", async () => {
      batchMock.mockResolvedValue(undefined);

      const { country, ...messageWithoutCountry } = message;

      await repository.save(messageWithoutCountry);

      const [queries] = batchMock.mock.calls[0];

      expect(queries[0].params[2]).toBeNull();
      expect(queries[1].params[2]).toBeNull();
    });
  });

  describe("findById", () => {
    it("should return a message by id", async () => {
      executeMock.mockResolvedValue({
        first: () => cassandraRow,
      });

      const result = await repository.findById(100);

      expect(result).toEqual(message);
      expect(executeMock).toHaveBeenCalledTimes(1);
    });

    it("should return undefined when message does not exist", async () => {
      executeMock.mockResolvedValue({
        first: () => null,
      });

      const result = await repository.findById(999);

      expect(result).toBeUndefined();
    });
  });

  describe("findByTopicId", () => {
    it("should return messages by topic in ascending order", async () => {
      executeMock.mockResolvedValue({
        rows: [cassandraRow],
      });

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([message]);

      const [query, params, options] = executeMock.mock.calls[0];

      expect(query).toContain("tbl_message_by_topic");
      expect(query).toContain("ORDER BY id ASC");
      expect(params).toEqual([10]);
      expect(options).toEqual({ prepare: true });
    });

    it("should return messages in descending order", async () => {
      executeMock.mockResolvedValue({
        rows: [cassandraRow],
      });

      await repository.findByTopicId(10, "desc");

      const [query] = executeMock.mock.calls[0];

      expect(query).toContain("ORDER BY id DESC");
    });

    it("should return an empty array when topic has no messages", async () => {
      executeMock.mockResolvedValue({ rows: [] });

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([]);
    });
  });

  describe("findAll", () => {
    it("should return all messages", async () => {
      executeMock.mockResolvedValue({
        rows: [cassandraRow],
      });

      const result = await repository.findAll();

      expect(result).toEqual([message]);
      expect(executeMock.mock.calls[0][0]).toContain("distcomp.tbl_message");
    });

    it("should return an empty array when there are no messages", async () => {
      executeMock.mockResolvedValue({ rows: [] });

      const result = await repository.findAll();

      expect(result).toEqual([]);
    });
  });

  describe("update", () => {
    it("should update an existing message", async () => {
      executeMock.mockResolvedValue({
        first: () => cassandraRow,
      });
      batchMock.mockResolvedValue(undefined);

      const result = await repository.update(100, {
        content: "Updated message",
      });

      expect(result).toEqual({
        ...message,
        content: "Updated message",
      });

      expect(batchMock).toHaveBeenCalledTimes(1);

      const [queries] = batchMock.mock.calls[0];

      expect(queries).toHaveLength(3);
      expect(queries[0].query).toContain("UPDATE distcomp.tbl_message");
      expect(queries[1].query).toContain(
        "DELETE FROM distcomp.tbl_message_by_topic",
      );
      expect(queries[2].query).toContain(
        "INSERT INTO distcomp.tbl_message_by_topic",
      );
    });

    it("should return undefined when message does not exist", async () => {
      executeMock.mockResolvedValue({
        first: () => null,
      });

      const result = await repository.update(999, {
        content: "Updated message",
      });

      expect(result).toBeUndefined();
      expect(batchMock).not.toHaveBeenCalled();
    });
  });

  describe("delete", () => {
    it("should delete an existing message from both tables", async () => {
      executeMock.mockResolvedValue({
        first: () => cassandraRow,
      });
      batchMock.mockResolvedValue(undefined);

      const result = await repository.delete(100);

      expect(result).toBe(true);
      expect(batchMock).toHaveBeenCalledTimes(1);

      const [queries] = batchMock.mock.calls[0];

      expect(queries).toHaveLength(2);
      expect(queries[0].query).toContain("DELETE FROM distcomp.tbl_message");
      expect(queries[1].query).toContain(
        "DELETE FROM distcomp.tbl_message_by_topic",
      );
    });

    it("should return false when message does not exist", async () => {
      executeMock.mockResolvedValue({
        first: () => null,
      });

      const result = await repository.delete(999);

      expect(result).toBe(false);
      expect(batchMock).not.toHaveBeenCalled();
    });
  });
});

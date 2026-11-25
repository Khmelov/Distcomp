import { NextFunction, Request, Response } from "express";
import { MessageRepository } from "../../repositories/MessageRepository";
import { MessageController } from "../MessageController";

describe("MessageController", () => {
  let controller: MessageController;
  let repository: jest.Mocked<MessageRepository>;
  let req: Partial<Request>;
  let res: Partial<Response>;
  let next: jest.MockedFunction<NextFunction>;

  const message = {
    id: 100,
    topicId: 10,
    country: "US",
    content: "Test message",
  };

  beforeEach(() => {
    repository = {
      save: jest.fn(),
      findAll: jest.fn(),
      findByTopicId: jest.fn(),
      findById: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
    } as unknown as jest.Mocked<MessageRepository>;

    controller = new MessageController(repository);

    req = {
      body: {},
      params: {},
      query: {},
    };

    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
      send: jest.fn().mockReturnThis(),
    };

    next = jest.fn();
  });

  describe("create", () => {
    it("should create a message and return 201", async () => {
      req.body = {
        topicId: 10,
        country: "US",
        content: "Test message",
      };

      repository.save.mockResolvedValue(undefined);

      const dateNowSpy = jest.spyOn(Date, "now").mockReturnValue(100);

      await controller.create(req as Request, res as Response, next);

      expect(repository.save).toHaveBeenCalledWith(message);
      expect(res.status).toHaveBeenCalledWith(201);
      expect(res.json).toHaveBeenCalledWith(message);
      expect(next).not.toHaveBeenCalled();

      dateNowSpy.mockRestore();
    });

    it("should create a message without country", async () => {
      req.body = {
        topicId: 10,
        content: "Test message",
      };

      repository.save.mockResolvedValue(undefined);

      jest.spyOn(Date, "now").mockReturnValue(100);

      await controller.create(req as Request, res as Response, next);

      expect(repository.save).toHaveBeenCalledWith({
        id: 100,
        topicId: 10,
        country: undefined,
        content: "Test message",
      });

      jest.restoreAllMocks();
    });

    it("should return validation error when topicId is missing", async () => {
      req.body = {
        content: "Test message",
      };

      await controller.create(req as Request, res as Response, next);

      expect(repository.save).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "topicId and content are required",
        }),
      );
    });

    it("should return validation error when content is missing", async () => {
      req.body = {
        topicId: 10,
      };

      await controller.create(req as Request, res as Response, next);

      expect(repository.save).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "topicId and content are required",
        }),
      );
    });

    it("should pass repository errors to next", async () => {
      const error = new Error("Database error");

      req.body = {
        topicId: 10,
        content: "Test message",
      };

      repository.save.mockRejectedValue(error);

      await controller.create(req as Request, res as Response, next);

      expect(next).toHaveBeenCalledWith(error);
    });
  });

  describe("findAll", () => {
    it("should return all messages", async () => {
      repository.findAll.mockResolvedValue([message]);

      await controller.findAll(req as Request, res as Response, next);

      expect(repository.findAll).toHaveBeenCalled();
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith([message]);
    });

    it("should pass repository errors to next", async () => {
      const error = new Error("Database error");

      repository.findAll.mockRejectedValue(error);

      await controller.findAll(req as Request, res as Response, next);

      expect(next).toHaveBeenCalledWith(error);
    });
  });

  describe("findByTopicId", () => {
    it("should return messages in ascending order by default", async () => {
      req.params = { topicId: "10" };
      req.query = {};

      repository.findByTopicId.mockResolvedValue([message]);

      await controller.findByTopicId(req as Request, res as Response, next);

      expect(repository.findByTopicId).toHaveBeenCalledWith(10, "asc");
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith([message]);
    });

    it("should return messages in descending order", async () => {
      req.params = { topicId: "10" };
      req.query = { sortOrder: "desc" };

      repository.findByTopicId.mockResolvedValue([message]);

      await controller.findByTopicId(req as Request, res as Response, next);

      expect(repository.findByTopicId).toHaveBeenCalledWith(10, "desc");
    });

    it("should return validation error for invalid topicId", async () => {
      req.params = { topicId: "invalid" };

      await controller.findByTopicId(req as Request, res as Response, next);

      expect(repository.findByTopicId).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Invalid topicId",
        }),
      );
    });

    it("should return validation error for invalid sortOrder", async () => {
      req.params = { topicId: "10" };
      req.query = { sortOrder: "wrong" };

      await controller.findByTopicId(req as Request, res as Response, next);

      expect(repository.findByTopicId).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Invalid sortOrder",
        }),
      );
    });
  });

  describe("findById", () => {
    it("should return a message by id", async () => {
      req.params = { id: "100" };

      repository.findById.mockResolvedValue(message);

      await controller.findById(req as Request, res as Response, next);

      expect(repository.findById).toHaveBeenCalledWith(100);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith(message);
    });

    it("should return not found when message does not exist", async () => {
      req.params = { id: "999" };

      repository.findById.mockResolvedValue(undefined);

      await controller.findById(req as Request, res as Response, next);

      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Message not found",
        }),
      );
    });

    it("should return validation error for invalid id", async () => {
      req.params = { id: "invalid" };

      await controller.findById(req as Request, res as Response, next);

      expect(repository.findById).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Invalid message id",
        }),
      );
    });
  });

  describe("update", () => {
    it("should update a message", async () => {
      req.params = { id: "100" };
      req.body = {
        topicId: 20,
        country: "UK",
        content: "Updated message",
      };

      const updatedMessage = {
        id: 100,
        topicId: 20,
        country: "UK",
        content: "Updated message",
      };

      repository.update.mockResolvedValue(updatedMessage);

      await controller.update(req as Request, res as Response, next);

      expect(repository.update).toHaveBeenCalledWith(100, {
        topicId: 20,
        country: "UK",
        content: "Updated message",
      });

      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith(updatedMessage);
    });

    it("should return validation error when topicId is missing", async () => {
      req.params = { id: "100" };
      req.body = {
        content: "Updated message",
      };

      await controller.update(req as Request, res as Response, next);

      expect(repository.update).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "topicId and content are required",
        }),
      );
    });

    it("should return not found when message does not exist", async () => {
      req.params = { id: "999" };
      req.body = {
        topicId: 10,
        content: "Updated message",
      };

      repository.update.mockResolvedValue(undefined);

      await controller.update(req as Request, res as Response, next);

      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Message not found",
        }),
      );
    });

    it("should return validation error for invalid id", async () => {
      req.params = { id: "invalid" };
      req.body = {
        topicId: 10,
        content: "Updated message",
      };

      await controller.update(req as Request, res as Response, next);

      expect(repository.update).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Invalid message id",
        }),
      );
    });
  });

  describe("delete", () => {
    it("should delete a message and return 204", async () => {
      req.params = { id: "100" };

      repository.delete.mockResolvedValue(true);

      await controller.delete(req as Request, res as Response, next);

      expect(repository.delete).toHaveBeenCalledWith(100);
      expect(res.status).toHaveBeenCalledWith(204);
      expect(res.send).toHaveBeenCalled();
    });

    it("should return not found when message does not exist", async () => {
      req.params = { id: "999" };

      repository.delete.mockResolvedValue(false);

      await controller.delete(req as Request, res as Response, next);

      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Message not found",
        }),
      );
    });

    it("should return validation error for invalid id", async () => {
      req.params = { id: "invalid" };

      await controller.delete(req as Request, res as Response, next);

      expect(repository.delete).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledWith(
        expect.objectContaining({
          message: "Invalid message id",
        }),
      );
    });
  });
});

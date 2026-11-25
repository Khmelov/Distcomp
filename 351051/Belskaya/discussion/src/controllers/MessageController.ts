import { NextFunction, Request, Response } from "express";
import { NotFoundError, ValidationError } from "../errors/AppError";
import { MessageRepository } from "../repositories/MessageRepository";

export class MessageController {
  constructor(private readonly repository = new MessageRepository()) {}

  create = async (
    req: Request,
    res: Response,
    next: NextFunction,
  ): Promise<void> => {
    try {
      const { country, topicId, content } = req.body;

      if (
        topicId === undefined ||
        !Number.isInteger(Number(topicId)) ||
        !content
      ) {
        throw new ValidationError("topicId and content are required");
      }

      const message = {
        id: Date.now(),
        topicId: Number(topicId),
        country: country || undefined,
        content,
      };

      await this.repository.save(message);

      res.status(201).json(message);
    } catch (err) {
      next(err);
    }
  };

  findAll = async (
    _req: Request,
    res: Response,
    next: NextFunction,
  ): Promise<void> => {
    try {
      const messages = await this.repository.findAll();

      res.status(200).json(messages);
    } catch (err) {
      next(err);
    }
  };

  findByTopicId = async (
    req: Request,
    res: Response,
    next: NextFunction,
  ): Promise<void> => {
    try {
      const topicId = Number(req.params.topicId);
      const sortOrder = req.query.sortOrder ?? "asc";

      if (!Number.isInteger(topicId))
        throw new ValidationError("Invalid topicId");

      if (sortOrder !== "asc" && sortOrder !== "desc") {
        throw new ValidationError("Invalid sortOrder");
      }

      const messages = await this.repository.findByTopicId(topicId, sortOrder);

      res.status(200).json(messages);
    } catch (err) {
      next(err);
    }
  };

  findById = async (
    req: Request,
    res: Response,
    next: NextFunction,
  ): Promise<void> => {
    try {
      const id = Number(req.params.id);

      if (!Number.isInteger(id))
        throw new ValidationError("Invalid message id");

      const message = await this.repository.findById(id);

      if (!message) throw new NotFoundError("Message not found");

      res.status(200).json(message);
    } catch (err) {
      next(err);
    }
  };

  update = async (
    req: Request,
    res: Response,
    next: NextFunction,
  ): Promise<void> => {
    try {
      const id = Number(req.params.id);
      const { topicId, country, content } = req.body;

      if (!Number.isInteger(id))
        throw new ValidationError("Invalid message id");

      if (
        topicId === undefined ||
        !Number.isInteger(Number(topicId)) ||
        !content
      ) {
        throw new ValidationError("topicId and content are required");
      }

      const message = await this.repository.update(id, {
        topicId: Number(topicId),
        content,
        ...(country !== undefined ? { country } : {}),
      });

      if (!message) throw new NotFoundError("Message not found");

      res.status(200).json(message);
    } catch (err) {
      next(err);
    }
  };

  delete = async (
    req: Request,
    res: Response,
    next: NextFunction,
  ): Promise<void> => {
    try {
      const id = Number(req.params.id);

      if (!Number.isInteger(id))
        throw new ValidationError("Invalid message id");

      const deleted = await this.repository.delete(id);

      if (!deleted) throw new NotFoundError("Message not found");

      res.status(204).send();
    } catch (err) {
      next(err);
    }
  };
}

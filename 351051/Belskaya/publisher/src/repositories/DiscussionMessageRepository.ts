import { Message } from '../entities';
import {
  AppError,
  ConflictError,
  NotFoundError,
  UnprocessableEntityError,
  ValidationError,
} from '../errors/AppError';
import { IRepository, PaginationParams } from './IRepository';

const DISCUSSION_URL =
  process.env.DISCUSSION_URL || 'http://localhost:24130/api/v1.0/messages';

export class DiscussionMessageRepository implements IRepository<Message> {
  private handleError(status: number): never {
    const message = `Discussion error: ${status}`;

    switch (status) {
      case 400:
        throw new ValidationError(message);
      case 403:
        throw new ConflictError(message);
      case 404:
        throw new NotFoundError(message);
      case 422:
        throw new UnprocessableEntityError(message);
      default:
        throw new AppError(status, `${status}01`, message);
    }
  }
  async findAll(): Promise<Message[]> {
    const response = await fetch(`${DISCUSSION_URL}`);

    if (!response.ok) this.handleError(response.status);

    return (await response.json()) as Message[];
  }

  async findById(id: number): Promise<Message | undefined> {
    const response = await fetch(`${DISCUSSION_URL}/${id}`);

    if (response.status === 404) return undefined;

    if (!response.ok) this.handleError(response.status);

    return (await response.json()) as Message;
  }

  async findByTopicId(
    topicId: number,
    sortOrder?: PaginationParams['sortOrder']
  ): Promise<Message[]> {
    const query = new URLSearchParams();

    if (sortOrder) {
      query.set('sortOrder', sortOrder);
    }

    const url = `${DISCUSSION_URL}/by-topic/${topicId}?${query}`;

    const response = await fetch(url);

    if (!response.ok) this.handleError(response.status);

    return (await response.json()) as Message[];
  }
  async save(entity: Omit<Message, 'id'>): Promise<Message> {
    const response = await fetch(DISCUSSION_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(entity),
    });

    if (!response.ok) this.handleError(response.status);

    return (await response.json()) as Message;
  }

  async update(
    id: number,
    entity: Partial<Message>
  ): Promise<Message | undefined> {
    const response = await fetch(`${DISCUSSION_URL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(entity),
    });

    if (response.status === 404) return undefined;

    if (!response.ok) this.handleError(response.status);

    return (await response.json()) as Message;
  }

  async delete(id: number): Promise<boolean> {
    const response = await fetch(`${DISCUSSION_URL}/${id}`, {
      method: 'DELETE',
    });

    if (response.status === 404) return false;

    if (!response.ok) this.handleError(response.status);

    return true;
  }

  async existsById(id: number): Promise<boolean> {
    return (await this.findById(id)) !== undefined;
  }
}

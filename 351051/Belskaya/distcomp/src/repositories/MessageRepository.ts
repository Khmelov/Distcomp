import { Message } from '../entities';
import { InMemoryRepository } from './InMemoryRepository';

export class MessageRepository extends InMemoryRepository<Message> {
  findByTopicId(topicId: number): Message[] {
    return this.findAll().filter((m) => m.topicId === topicId);
  }
}
import { Topic } from '../entities';
import { InMemoryRepository } from './InMemoryRepository';

export class TopicRepository extends InMemoryRepository<Topic> {
  findByAuthorId(authorId: number): Topic[] {
    return this.findAll().filter((t) => t.authorId === authorId);
  }
}
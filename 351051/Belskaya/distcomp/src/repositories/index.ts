import { IdGenerator } from '../utils/idGenerator';
import { AuthorRepository } from './AuthorRepository';
import { MarkerRepository } from './MarkerRepository';
import { MessageRepository } from './MessageRepository';
import { TopicMarkerRepository } from './TopicMarkerRepository';
import { TopicRepository } from './TopicRepository';

export const authorRepository = new AuthorRepository();
export const topicRepository = new TopicRepository();
export const markerRepository = new MarkerRepository();
export const messageRepository = new MessageRepository();
export const topicMarkerRepository = new TopicMarkerRepository();

export function resetRepositories(): void {
  authorRepository.clear();
  topicRepository.clear();
  markerRepository.clear();
  messageRepository.clear();
  IdGenerator.reset();
}

export { InMemoryRepository } from './InMemoryRepository';
export { IRepository } from './IRepository';


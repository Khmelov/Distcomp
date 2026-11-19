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

export { InMemoryRepository } from './InMemoryRepository';
export { IRepository } from './IRepository';

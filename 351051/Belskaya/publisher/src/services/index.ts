import {
  authorRepo,
  topicRepo,
  markerRepo,
  messageRepo,
} from '../repositories';
import { AuthorService } from './AuthorService';
import { MarkerService } from './MarkerService';
import { MessageService } from './MessageService';
import { TopicService } from './TopicService';

export const authorService = new AuthorService(authorRepo, topicRepo);
export const topicService = new TopicService(topicRepo, authorRepo);
export const markerService = new MarkerService(markerRepo);
export const messageService = new MessageService(messageRepo, topicRepo);

export { AuthorService, MarkerService, MessageService, TopicService };

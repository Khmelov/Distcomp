import {
   authorRepository,
   markerRepository,
   messageRepository,
   topicRepository,
} from '../repositories';

import { AuthorService } from './AuthorService';
import { MarkerService } from './MarkerService';
import { MessageService } from './MessageService';
import { TopicService } from './TopicService';

export const authorService = new AuthorService(authorRepository);
export const topicService = new TopicService(topicRepository, authorRepository);
export const markerService = new MarkerService(markerRepository);
export const messageService = new MessageService(messageRepository, topicRepository);

export { AuthorService, MarkerService, MessageService, TopicService };

import { DiscussionMessageRepository } from './DiscussionMessageRepository';
import { PrismaAuthorRepository } from './PrismaAuthorRepository';
import { PrismaMarkerRepository } from './PrismaMarkerRepository';
import { PrismaTopicRepository } from './PrismaTopicRepository';

const authorRepo = new PrismaAuthorRepository();
const topicRepo = new PrismaTopicRepository();
const markerRepo = new PrismaMarkerRepository();
const messageRepo = new DiscussionMessageRepository();

export { authorRepo, markerRepo, messageRepo, topicRepo };

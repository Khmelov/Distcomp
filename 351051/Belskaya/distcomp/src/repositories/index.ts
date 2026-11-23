import { PrismaAuthorRepository } from './PrismaAuthorRepository';
import { PrismaMarkerRepository } from './PrismaMarkerRepository';
import { PrismaMessageRepository } from './PrismaMessageRepository';
import { PrismaTopicRepository } from './PrismaTopicRepository';

const authorRepo = new PrismaAuthorRepository();
const topicRepo = new PrismaTopicRepository();
const markerRepo = new PrismaMarkerRepository();
const messageRepo = new PrismaMessageRepository();

export { authorRepo, markerRepo, messageRepo, topicRepo };

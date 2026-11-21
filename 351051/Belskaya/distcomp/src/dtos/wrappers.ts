import {
   AuthorResponseTo,
   MarkerResponseTo,
   MessageResponseTo,
   TopicResponseTo,
} from './responses';

export interface AuthorResponseWrapper {
  author: AuthorResponseTo;
}

export interface TopicResponseWrapper {
  topic: TopicResponseTo;
}

export interface MarkerResponseWrapper {
  marker: MarkerResponseTo;
}

export interface MessageResponseWrapper {
  message: MessageResponseTo;
}
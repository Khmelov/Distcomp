import {
   AuthorRequestTo,
   MarkerRequestTo,
   MessageRequestTo,
   TopicRequestTo,
} from '../dtos/requests';
import {
   AuthorResponseTo,
   MarkerResponseTo,
   MessageResponseTo,
   TopicResponseTo,
} from '../dtos/responses';
import { Author, Marker, Message, Topic } from '../entities';

// ===== Author =====
export function toAuthorEntity(req: AuthorRequestTo, id: number): Author {
  return {
    id,
    login: req.login,
    password: req.password,
    firstname: req.firstname,
    lastname: req.lastname,
  };
}

export function toAuthorResponse(entity: Author): AuthorResponseTo {
  return {
    id: entity.id,
    login: entity.login,
    password: entity.password,
    firstname: entity.firstname,
    lastname: entity.lastname,
  };
}

// ===== Topic =====
export function toTopicEntity(
  req: TopicRequestTo,
  id: number,
  created: Date,
  modified: Date
): Topic {
  return {
    id,
    authorId: req.authorId,
    title: req.title,
    content: req.content,
    created,
    modified,
  };
}

export function toTopicResponse(entity: Topic): TopicResponseTo {
  return {
    id: entity.id,
    authorId: entity.authorId,
    title: entity.title,
    content: entity.content,
    created: entity.created.toISOString(),
    modified: entity.modified.toISOString(),
  };
}

// ===== Marker =====
export function toMarkerEntity(req: MarkerRequestTo, id: number): Marker {
  return {
    id,
    name: req.name,
  };
}

export function toMarkerResponse(entity: Marker): MarkerResponseTo {
  return {
    id: entity.id,
    name: entity.name,
  };
}

// ===== Message =====
export function toMessageEntity(req: MessageRequestTo, id: number): Message {
  return {
    id,
    topicId: req.topicId,
    content: req.content,
  };
}

export function toMessageResponse(entity: Message): MessageResponseTo {
  return {
    id: entity.id,
    topicId: entity.topicId,
    content: entity.content,
  };
}
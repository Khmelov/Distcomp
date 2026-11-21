export interface AuthorResponseTo {
  id: number;
  login: string;
  password: string;
  firstname: string;
  lastname: string;
}

export interface TopicResponseTo {
  id: number;
  authorId: number;
  title: string;
  content: string;
  created: string;
  modified: string;
}

export interface MarkerResponseTo {
  id: number;
  name: string;
}

export interface MessageResponseTo {
  id: number;
  topicId: number;
  content: string;
}
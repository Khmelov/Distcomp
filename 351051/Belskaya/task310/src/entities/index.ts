export interface Author {
  id: number;
  login: string;
  password: string;
  firstname: string;
  lastname: string;
}

export interface Topic {
  id: number;
  authorId: number;
  title: string;
  content: string;
  created: Date;
  modified: Date;
}

export interface Marker {
  id: number;
  name: string;
}

export interface Message {
  id: number;
  topicId: number;
  content: string;
}
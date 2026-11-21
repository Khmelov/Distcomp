export interface AuthorRequestTo {
  login: string;
  password: string;
  firstname: string;
  lastname: string;
}

export interface TopicRequestTo {
  authorId: number;
  title: string;
  content: string;
}

export interface MarkerRequestTo {
  name: string;
}

export interface MessageRequestTo {
  topicId: number;
  content: string;
}
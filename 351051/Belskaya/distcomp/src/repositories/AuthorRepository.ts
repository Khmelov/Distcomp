import { Author } from '../entities';
import { InMemoryRepository } from './InMemoryRepository';

export class AuthorRepository extends InMemoryRepository<Author> {
  findByLogin(login: string): Author | undefined {
    return this.findAll().find((a) => a.login === login);
  }
}
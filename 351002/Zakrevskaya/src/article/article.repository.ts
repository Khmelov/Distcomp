import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { BaseRepository } from '../common/repositories/base.repository';
import { Article } from './article.entity';

@Injectable()
export class ArticleRepository extends BaseRepository<Article> {
  constructor(
    @InjectRepository(Article)
    public repository: Repository<Article>,
  ) {
    super(repository);
  }

  async findByIdWithRelations(id: number): Promise<Article | null> {
    return this.repository.findOne({
      where: { id },
      relations: ['user', 'markers'],
    });
  }

  async findAllWithRelations(page: number = 1, limit: number = 10): Promise<[Article[], number]> {
    const skip = (page - 1) * limit;
    return this.repository.findAndCount({
      relations: ['user', 'markers'],
      skip,
      take: limit,
    });
  }

  async findOneByTitle(title: string): Promise<Article | null> {
    return this.repository.findOne({
      where: { title },
    });
  }
}
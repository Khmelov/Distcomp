import { Injectable, NotFoundException, BadRequestException, HttpException, HttpStatus } from '@nestjs/common';
import { ArticleRepository } from './article.repository';
import { UserRepository } from '../user/user.repository';
import { MarkerRepository } from '../marker/marker.repository';
import { CreateArticleDto, ArticleResponseDto } from './dto/article.dto';
import { Article } from './article.entity';
import { Marker } from '../marker/marker.entity';

@Injectable()
export class ArticleService {
  constructor(
    private readonly articleRepository: ArticleRepository,
    private readonly userRepository: UserRepository,
    private readonly markerRepository: MarkerRepository,
  ) {}

  async create(createArticleDto: CreateArticleDto): Promise<ArticleResponseDto> {
    this.validateArticleDto(createArticleDto);
    
    const existingArticle = await this.articleRepository.findOneByTitle(createArticleDto.title);
    if (existingArticle) {
      throw new HttpException('Article with this title already exists', HttpStatus.FORBIDDEN);
    }
    
    const user = await this.userRepository.findById(createArticleDto.userId);
    if (!user) throw new NotFoundException('User not found');

    let markers: Marker[] = [];
    if (createArticleDto.markers && createArticleDto.markers.length > 0) {
      markers = await this.createOrFindMarkers(createArticleDto.markers);
    }

    const articleData = {
      title: createArticleDto.title,
      content: createArticleDto.content,
      user: user,
      markers: markers,
    };

    const article = await this.articleRepository.create(articleData);
    
    const articleWithRelations = await this.articleRepository.findByIdWithRelations(article.id);
    if (!articleWithRelations) {
      throw new NotFoundException('Article not found after creation');
    }
    
    return this.toResponseDto(articleWithRelations);
  }

  async update(id: number, updateArticleDto: CreateArticleDto): Promise<ArticleResponseDto> {
    this.validateArticleDto(updateArticleDto);
    
    const article = await this.articleRepository.findByIdWithRelations(id);
    if (!article) throw new NotFoundException('Article not found');

    if (updateArticleDto.title !== article.title) {
      const existingArticle = await this.articleRepository.findOneByTitle(updateArticleDto.title);
      if (existingArticle && existingArticle.id !== id) {
        throw new HttpException('Article with this title already exists', HttpStatus.FORBIDDEN);
      }
    }

    if (updateArticleDto.userId) {
      const user = await this.userRepository.findById(updateArticleDto.userId);
      if (!user) throw new NotFoundException('User not found');
      article.user = user;
    }

    article.title = updateArticleDto.title;
    article.content = updateArticleDto.content;
    article.modified = new Date();

    if (updateArticleDto.markers) {
      const markers = await this.createOrFindMarkers(updateArticleDto.markers);
      article.markers = markers;
    }

    await this.articleRepository.repository.save(article);
    
    const updated = await this.articleRepository.findByIdWithRelations(id);
    if (!updated) throw new NotFoundException('Article not found after update');
    
    return this.toResponseDto(updated);
  }

  async delete(id: number): Promise<void> {
    const article = await this.articleRepository.findByIdWithRelations(id);
    if (!article) throw new NotFoundException('Article not found');
    
    const markerIds = article.markers?.map(m => m.id) || [];
    
    await this.articleRepository.delete(id);
    
    for (const markerId of markerIds) {
      const articlesWithMarker = await this.articleRepository.repository
        .createQueryBuilder('article')
        .innerJoin('article.markers', 'marker')
        .where('marker.id = :markerId', { markerId })
        .getCount();
      
      if (articlesWithMarker === 0) {
        await this.markerRepository.delete(markerId);
      }
    }
  }

  async findById(id: number): Promise<ArticleResponseDto> {
    const article = await this.articleRepository.findByIdWithRelations(id);
    if (!article) throw new NotFoundException('Article not found');
    return this.toResponseDto(article);
  }

  async findAll(page: number = 1, limit: number = 10): Promise<ArticleResponseDto[]> {
    const [articles] = await this.articleRepository.findAllWithRelations(page, limit);
    return articles.map(article => this.toResponseDto(article));
  }

  private async createOrFindMarkers(markerNames: string[]): Promise<Marker[]> {
    const markers: Marker[] = [];
    
    for (const name of markerNames) {
      let marker = await this.markerRepository.findOneByName(name);
      if (!marker) {
        marker = await this.markerRepository.create({ name });
      }
      markers.push(marker);
    }
    
    return markers;
  }

  private validateArticleDto(dto: CreateArticleDto) {
    if (dto.title.length < 2 || dto.title.length > 64) {
      throw new BadRequestException('Title must be 2-64 characters');
    }
    if (dto.content.length < 4 || dto.content.length > 2048) {
      throw new BadRequestException('Content must be 4-2048 characters');
    }
    if (dto.markers && dto.markers.length > 0) {
      for (const marker of dto.markers) {
        if (marker.length < 2 || marker.length > 32) {
          throw new BadRequestException('Marker name must be 2-32 characters');
        }
      }
    }
  }

  private toResponseDto(article: Article): ArticleResponseDto {
    return {
      id: article.id,
      title: article.title,
      content: article.content,
      created: article.created.toISOString(),
      modified: article.modified.toISOString(),
      userId: article.user?.id,
      markerIds: article.markers?.map(m => m.id) || [],
    };
  }
}
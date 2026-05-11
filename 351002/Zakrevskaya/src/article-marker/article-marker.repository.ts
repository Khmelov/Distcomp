import { Injectable } from '@nestjs/common';

export interface ArticleMarkerEntity {
  articleId: number;
  markerId: number;
}

@Injectable()
export class ArticleMarkerRepository {
  private relations: ArticleMarkerEntity[] = [];

  addRelation(articleId: number, markerId: number): void {
    this.relations.push({ articleId, markerId });
  }

  removeByArticle(articleId: number): void {
    this.relations = this.relations.filter(r => r.articleId !== articleId);
  }

  removeByMarker(markerId: number): void {
    this.relations = this.relations.filter(r => r.markerId !== markerId);
  }

  findMarkersByArticle(articleId: number): number[] {
    return this.relations
      .filter(r => r.articleId === articleId)
      .map(r => r.markerId);
  }

  findArticlesByMarker(markerId: number): number[] {
    return this.relations
      .filter(r => r.markerId === markerId)
      .map(r => r.articleId);
  }
}
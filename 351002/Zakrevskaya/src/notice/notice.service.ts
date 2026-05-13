import { Injectable, NotFoundException } from '@nestjs/common';
import { NoticeRepository } from './notice.repository';
import { CreateNoticeDto, NoticeResponseDto } from './dto/notice.dto';

@Injectable()
export class NoticeService {
  constructor(private readonly noticeRepository: NoticeRepository) {}

  async create(dto: CreateNoticeDto, port: number): Promise<NoticeResponseDto> {
    const n = await this.noticeRepository.create({ articleId: dto.articleId, country: 'RU', content: dto.content }, port);
    return this.toResponseDto(n);
  }

  // port теперь перед необязательными aid и country
  async findById(id: number, port: number, aid?: number, country: string = 'RU'): Promise<NoticeResponseDto> {
    const n = await this.noticeRepository.findById(id, aid, country, port);
    if (!n) throw new NotFoundException('Notice not found');
    return this.toResponseDto(n);
  }

  async update(id: number, dto: CreateNoticeDto, port: number): Promise<NoticeResponseDto> {
    const existing = await this.noticeRepository.findById(id); 
    if (!existing) throw new NotFoundException('Notice not found');
    
    const aid = Number(existing.article_id || existing.articleId);
    await this.noticeRepository.update(id, aid, 'RU', dto.content, port);
    
    return { id: Number(id), content: dto.content, articleId: aid };
  }

  async delete(id: number, port: number, aid?: number, country: string = 'RU'): Promise<void> {
    let targetAid = aid;
    if (!targetAid) {
      const ex = await this.noticeRepository.findById(id);
      if (!ex) throw new NotFoundException('Notice not found');
      targetAid = Number(ex.article_id || ex.articleId);
    }
    await this.noticeRepository.delete(id, targetAid!, country);
  }

  async findAll(port: number, articleId: number | null, country: string = 'RU'): Promise<NoticeResponseDto[]> {
    const notices = await this.noticeRepository.findByArticle(articleId, country);
    return notices.map(n => this.toResponseDto(n));
  }

  private toResponseDto(n: any): NoticeResponseDto {
    return { id: Number(n.id), content: n.content, articleId: Number(n.article_id || n.articleId) };
  }
}
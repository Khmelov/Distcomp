import {
  BadRequestException,
  Injectable,
  NotFoundException,
  ServiceUnavailableException,
} from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { InjectRepository } from '@nestjs/typeorm';
import { AxiosError } from 'axios';
import { Repository } from 'typeorm';
import { firstValueFrom } from 'rxjs';
import { ReactionRequestTo } from '../dto/reaction-request.to';
import { ReactionResponseTo } from '../dto/reaction-response.to';
import { Issue } from '../../issue/entity/issue.entity';

@Injectable()
export class ReactionService {
  constructor(
    private readonly http: HttpService,
    @InjectRepository(Issue)
    private readonly issueRepository: Repository<Issue>,
  ) {}

  async getAll(): Promise<ReactionResponseTo[]> {
    try {
      const { data } = await firstValueFrom(
        this.http.get<ReactionResponseTo[]>('reactions'),
      );
      return data;
    } catch (e) {
      this.mapAxiosError(e);
    }
  }

  async getById(id: number): Promise<ReactionResponseTo> {
    try {
      const { data } = await firstValueFrom(
        this.http.get<ReactionResponseTo>(`reactions/${id}`),
      );
      return data;
    } catch (e) {
      this.mapAxiosError(e);
    }
  }

  async create(dto: ReactionRequestTo): Promise<ReactionResponseTo> {
    await this.ensureIssueExists(dto.issueId);
    try {
      const { data } = await firstValueFrom(
        this.http.post<ReactionResponseTo>('reactions', dto),
      );
      return data;
    } catch (e) {
      this.mapAxiosError(e);
    }
  }

  async update(id: number, dto: ReactionRequestTo): Promise<ReactionResponseTo> {
    await this.ensureIssueExists(dto.issueId);
    try {
      const { data } = await firstValueFrom(
        this.http.put<ReactionResponseTo>(`reactions/${id}`, dto),
      );
      return data;
    } catch (e) {
      this.mapAxiosError(e);
    }
  }

  async delete(id: number): Promise<void> {
    try {
      await firstValueFrom(this.http.delete(`reactions/${id}`));
    } catch (e) {
      this.mapAxiosError(e);
    }
  }

  async deleteByIssueId(issueId: number): Promise<void> {
    await firstValueFrom(this.http.delete(`reactions/by-issue/${issueId}`));
  }

  private async ensureIssueExists(issueId: number): Promise<void> {
    const ok = await this.issueRepository.exists({ where: { id: issueId } });
    if (!ok) throw new BadRequestException('Issue association is invalid');
  }

  private mapAxiosError(error: unknown): never {
    if (error instanceof AxiosError) {
      const status = error.response?.status;
      const msg = (error.response?.data as { message?: string })?.message;

      if (status === 404) {
        throw new NotFoundException(msg ?? 'Reaction not found');
      }
      if (error.code === 'ECONNREFUSED' || error.code === 'ETIMEDOUT') {
        throw new ServiceUnavailableException('Discussion service unavailable');
      }
    }
    throw error;
  }
}

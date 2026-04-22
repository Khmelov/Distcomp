import {
  BadRequestException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { IssueRequestTo } from '../dto/issue-request.to';
import { IssueResponseTo } from '../dto/issue-response.to';
import { Issue } from '../entity/issue.entity';
import { QueryFailedError, Repository } from 'typeorm';
import { Mark } from '../../mark/entity/mark.entity';
import { ReactionService } from '../../reaction/service/reaction.service';

@Injectable()
export class IssueService {
  constructor(
    @InjectRepository(Issue)
    private readonly repository: Repository<Issue>,
    @InjectRepository(Mark)
    private readonly markRepository: Repository<Mark>,
    private readonly reactionService: ReactionService,
  ) {}

  async getAll(): Promise<IssueResponseTo[]> {
    const issues = await this.repository.find();
    return issues.map((issue) => this.toResponse(issue));
  }

  async getById(id: number): Promise<IssueResponseTo> {
    const issue = await this.repository.findOne({ where: { id } });
    if (!issue) throw new NotFoundException(`Issue with id=${id} not found`);
    return this.toResponse(issue);
  }

  async create(dto: IssueRequestTo): Promise<IssueResponseTo> {
    const marks = await this.resolveMarks(dto.marks);
    const entity = this.repository.create({
      ...this.toEntity(dto),
      marks,
    });
    try {
      const created = await this.repository.save(entity);
      return this.toResponse(created);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async update(id: number, dto: IssueRequestTo): Promise<IssueResponseTo> {
    const existing = await this.repository.findOne({
      where: { id },
      relations: { marks: true },
    });
    if (!existing) throw new NotFoundException(`Issue with id=${id} not found`);

    const marks = await this.resolveMarks(dto.marks);
    const updatedEntity = this.repository.merge(existing, {
      ...this.toEntity(dto),
      marks,
    });

    const previousMarkIds = new Set((existing.marks ?? []).map((mark) => mark.id));
    try {
      const updated = await this.repository.save(updatedEntity);
      await this.deleteOrphanMarksByIds(previousMarkIds);
      return this.toResponse(updated);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async delete(id: number): Promise<void> {
    const issue = await this.repository.findOne({
      where: { id },
      relations: { marks: true },
    });
    if (!issue) throw new NotFoundException(`Issue with id=${id} not found`);
    const markIds = new Set((issue.marks ?? []).map((mark) => mark.id));
    await this.reactionService.deleteByIssueId(id);
    await this.repository.remove(issue);
    await this.deleteOrphanMarksByIds(markIds);
  }

  private handleDbError(error: unknown): never {
    if (error instanceof QueryFailedError) {
      const driverError = error.driverError as { code?: string };
      if (driverError?.code === '23505') {
        throw new ForbiddenException('Issue title must be unique');
      }
      if (driverError?.code === '23503') {
        throw new BadRequestException('Writer association is invalid');
      }
    }

    throw error;
  }

  private toEntity(dto: IssueRequestTo): Omit<Issue, 'id' | 'created' | 'modified' | 'writer' | 'marks'> {
    return {
      writerId: dto.writerId,
      title: dto.title,
      content: dto.content,
    };
  }

  private toResponse(entity: Issue): IssueResponseTo {
    return {
      id: entity.id,
      writerId: entity.writerId,
      title: entity.title,
      content: entity.content,
      created: entity.created.toISOString(),
      modified: entity.modified.toISOString(),
    };
  }

  private async resolveMarks(markNames?: string[]): Promise<Mark[]> {
    if (!markNames?.length) return [];

    const uniqueNames = [...new Set(markNames.map((name) => name.trim()).filter(Boolean))];
    if (!uniqueNames.length) return [];

    const existingMarks = await this.markRepository.find({
      where: uniqueNames.map((name) => ({ name })),
    });

    const existingByName = new Map(existingMarks.map((mark) => [mark.name, mark]));
    const missingNames = uniqueNames.filter((name) => !existingByName.has(name));

    const createdMarks = missingNames.length
      ? await this.markRepository.save(
          missingNames.map((name) => this.markRepository.create({ name })),
        )
      : [];

    return [...existingMarks, ...createdMarks];
  }

  private async deleteOrphanMarksByIds(markIds: Set<number>): Promise<void> {
    for (const markId of markIds) {
      const mark = await this.markRepository.findOne({
        where: { id: markId },
        relations: { issues: true },
      });

      if (mark && (!mark.issues || mark.issues.length === 0)) {
        await this.markRepository.remove(mark);
      }
    }
  }
}

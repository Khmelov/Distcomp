import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { MarkRequestTo } from '../dto/mark-request.to';
import { MarkResponseTo } from '../dto/mark-response.to';
import { Mark } from '../entity/mark.entity';
import { QueryFailedError, Repository } from 'typeorm';

@Injectable()
export class MarkService {
  constructor(
    @InjectRepository(Mark)
    private readonly repository: Repository<Mark>,
  ) {}

  async getAll(): Promise<MarkResponseTo[]> {
    const marks = await this.repository.find();
    return marks.map((mark) => this.toResponse(mark));
  }

  async getById(id: number): Promise<MarkResponseTo> {
    const mark = await this.repository.findOne({ where: { id } });
    if (!mark) throw new NotFoundException(`Mark with id=${id} not found`);
    return this.toResponse(mark);
  }

  async create(dto: MarkRequestTo): Promise<MarkResponseTo> {
    try {
      const created = await this.repository.save(this.repository.create(this.toEntity(dto)));
      return this.toResponse(created);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async update(id: number, dto: MarkRequestTo): Promise<MarkResponseTo> {
    const existing = await this.repository.findOne({ where: { id } });
    if (!existing) throw new NotFoundException(`Mark with id=${id} not found`);
    try {
      const updated = await this.repository.save(this.repository.merge(existing, this.toEntity(dto)));
      return this.toResponse(updated);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async delete(id: number): Promise<void> {
    const mark = await this.repository.findOne({ where: { id } });
    if (!mark) throw new NotFoundException(`Mark with id=${id} not found`);
    await this.repository.remove(mark);
  }

  private toEntity(dto: MarkRequestTo): Omit<Mark, 'id' | 'issues'> {
    return {
      name: dto.name,
    };
  }

  private handleDbError(error: unknown): never {
    if (error instanceof QueryFailedError) {
      const driverError = error.driverError as { code?: string };
      if (driverError?.code === '23505') {
        throw new ForbiddenException('Mark name must be unique');
      }
    }

    throw error;
  }

  private toResponse(entity: Mark): MarkResponseTo {
    return {
      id: entity.id,
      name: entity.name,
    };
  }
}

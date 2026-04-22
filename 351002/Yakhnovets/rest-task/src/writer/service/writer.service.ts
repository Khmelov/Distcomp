import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import * as bcrypt from 'bcryptjs';
import { WriterRequestTo } from '../dto/writer-request.to';
import { WriterResponseTo } from '../dto/writer-response.to';
import { Writer } from '../entity/writer.entity';
import { QueryFailedError, Repository } from 'typeorm';
import { ReactionService } from '../../reaction/service/reaction.service';
import { WriterRole } from '../../security/role.enum';

@Injectable()
export class WriterService {
  constructor(
    @InjectRepository(Writer)
    private readonly repository: Repository<Writer>,
    private readonly reactionService: ReactionService,
  ) {}

  async getAll(): Promise<WriterResponseTo[]> {
    const writers = await this.repository.find();
    return writers.map((writer) => this.toResponse(writer));
  }

  async getById(id: number): Promise<WriterResponseTo> {
    const writer = await this.repository.findOne({ where: { id } });
    if (!writer) throw new NotFoundException(`Writer with id=${id} not found`);
    return this.toResponse(writer);
  }

  async findAuthWriterByLogin(login: string): Promise<Writer | null> {
    return this.repository.findOne({ where: { login } });
  }

  async findWriterEntityByLogin(login: string): Promise<Writer | null> {
    return this.repository.findOne({ where: { login } });
  }

  async getWriterEntityById(id: number): Promise<Writer> {
    const writer = await this.repository.findOne({ where: { id } });
    if (!writer) throw new NotFoundException(`Writer with id=${id} not found`);
    return writer;
  }

  async create(dto: WriterRequestTo): Promise<WriterResponseTo> {
    const entity = this.repository.create(this.toEntity(dto));
    try {
      const created = await this.repository.save(entity);
      return this.toResponse(created);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async createSecure(dto: WriterRequestTo): Promise<WriterResponseTo> {
    const entity = this.repository.create(await this.toSecureEntity(dto));
    try {
      const created = await this.repository.save(entity);
      return this.toResponse(created);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async update(id: number, dto: WriterRequestTo): Promise<WriterResponseTo> {
    const existing = await this.repository.findOne({ where: { id } });
    if (!existing) throw new NotFoundException(`Writer with id=${id} not found`);

    const updatedEntity = this.repository.merge(existing, this.toEntity(dto));
    try {
      const updated = await this.repository.save(updatedEntity);
      return this.toResponse(updated);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async updateSecure(id: number, dto: WriterRequestTo): Promise<WriterResponseTo> {
    const existing = await this.repository.findOne({ where: { id } });
    if (!existing) throw new NotFoundException(`Writer with id=${id} not found`);

    const updatedEntity = this.repository.merge(existing, await this.toSecureEntity(dto));
    try {
      const updated = await this.repository.save(updatedEntity);
      return this.toResponse(updated);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async delete(id: number): Promise<void> {
    const writer = await this.repository.findOne({
      where: { id },
      relations: { issues: true },
    });
    if (!writer) throw new NotFoundException(`Writer with id=${id} not found`);
    for (const issue of writer.issues ?? []) {
      await this.reactionService.deleteByIssueId(issue.id);
    }
    await this.repository.remove(writer);
  }

  private handleDbError(error: unknown): never {
    if (error instanceof QueryFailedError) {
      const driverError = error.driverError as { code?: string };
      if (driverError?.code === '23505') {
        throw new ForbiddenException('Writer login must be unique');
      }
    }

    throw error;
  }

  private toEntity(dto: WriterRequestTo): Omit<Writer, 'id' | 'issues'> {
    return {
      login: dto.login,
      password: dto.password,
      firstname: dto.firstname,
      lastname: dto.lastname,
      role: dto.role ?? WriterRole.CUSTOMER,
    };
  }

  private async toSecureEntity(dto: WriterRequestTo): Promise<Omit<Writer, 'id' | 'issues'>> {
    return {
      login: dto.login,
      password: await bcrypt.hash(dto.password, 10),
      firstname: dto.firstname,
      lastname: dto.lastname,
      role: dto.role ?? WriterRole.CUSTOMER,
    };
  }

  private toResponse(entity: Writer): WriterResponseTo {
    return {
      id: entity.id,
      login: entity.login,
      password: entity.password,
      firstname: entity.firstname,
      lastname: entity.lastname,
    };
  }
}

import {
  HttpException,
  HttpStatus,
  Inject,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
  OnModuleInit,
} from '@nestjs/common';
import { NoteResponseTo } from './Dto/NoteResponseTo';
import { NoteRequestTo, UpdateNoteTo } from './Dto/NoteRequestTo';
import { InjectRepository } from '@nestjs/typeorm';
import { Article } from '../../entities/Article';
import { Repository } from 'typeorm';
import { Client, ClientKafka, Transport } from '@nestjs/microservices';
import {
  ADD_NEW_NOTE,
  DELETE_NOTE,
  GET_ALL_NOTES,
  GET_NOTE_BY_ID,
  UPDATE_NOTE,
} from 'src/constants/constants';
import { firstValueFrom } from 'rxjs';
import { CACHE_MANAGER } from '@nestjs/cache-manager';
import { Cache } from 'cache-manager';

@Injectable()
export class NoteService implements OnModuleInit {
  constructor(
    @InjectRepository(Article)
    private readonly articleRepository: Repository<Article>,
    @Inject(CACHE_MANAGER) private cacheManager: Cache,
  ) {}

  @Client({
    transport: Transport.KAFKA,
    options: {
      client: {
        clientId: 'note',
        brokers: ['localhost:9092'],
      },
      consumer: {
        groupId: 'note-consumer',
      },
    },
  })
  private client: ClientKafka;

  async onModuleInit() {
    this.client.subscribeToResponseOf(ADD_NEW_NOTE);
    this.client.subscribeToResponseOf(DELETE_NOTE);
    this.client.subscribeToResponseOf(UPDATE_NOTE);
    this.client.subscribeToResponseOf(GET_ALL_NOTES);
    this.client.subscribeToResponseOf(GET_NOTE_BY_ID);

    await this.client.connect();
  }

  async getAllNotes() {
    const cachedNotes = await this.cacheManager.get('notes');
    if (cachedNotes) {
      return cachedNotes;
    }
    const response = this.client.send<NoteResponseTo[], any>(GET_ALL_NOTES, '');
    await this.cacheManager.set('notes', response);
    return response;
  }

  async createNote(item: NoteRequestTo): Promise<NoteResponseTo> {
    try {
      const article = await this.articleRepository.findOne({
        where: { id: item.articleId },
      });
      if (!article) throw new NotFoundException();
      item.id = Math.floor(Math.random() * (10000 - 100 + 1)) + 100;
      const response = await firstValueFrom(
        this.client.send<NoteResponseTo, any>(ADD_NEW_NOTE, {
          value: JSON.stringify(item),
        }),
      );
      return response;
    } catch (err) {
      if (err instanceof NotFoundException) {
        throw new HttpException(
          {
            errorCode: 40403,
            errorMessage: 'Article does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      throw new InternalServerErrorException();
    }
  }

  async getNoteById(id: number): Promise<NoteResponseTo> {
    const cachedNote = await this.cacheManager.get(`note-${id}`);
    if (cachedNote) {
      return cachedNote as NoteResponseTo;
    }
    const response = await firstValueFrom(
      this.client.send<NoteResponseTo>(GET_NOTE_BY_ID, { noteId: id }),
    ).catch((err) => {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (err.response?.status === 404) {
        throw new HttpException(
          {
            errorCode: 40404,
            errorMessage: 'Note does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      throw new HttpException(
        {
          errorCode: 50000,
          errorMessage: 'Internal server error.',
        },
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    });
    await this.cacheManager.set(`note-${id}`, response);
    return response;
  }

  async deleteNote(id: number): Promise<void> {
    await firstValueFrom(
      this.client.send<void, any>(DELETE_NOTE, { noteId: id }),
    ).catch((err) => {
      console.log(err);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (err.response?.status === 404) {
        throw new HttpException(
          {
            errorCode: 40404,
            errorMessage: 'Note does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      throw new HttpException(
        {
          errorCode: 50000,
          errorMessage: 'Internal server error.',
        },
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    });
  }

  async updateNote(body: UpdateNoteTo): Promise<NoteResponseTo> {
    try {
      const cachedNote = await this.cacheManager.get(`note-${body.id}`);
      if (cachedNote) {
        await this.cacheManager.del(`note-${body.id}`);
      }
      const article = await this.articleRepository.findOne({
        where: { id: body.articleId },
      });
      if (!article) throw new NotFoundException();
      const response = await firstValueFrom(
        this.client.send<NoteResponseTo, any>(UPDATE_NOTE, {
          value: JSON.stringify(body),
        }),
      );
      await this.cacheManager.set(`note-${body.id}`, response);
      return response;
    } catch (err) {
      if (err instanceof NotFoundException) {
        throw new HttpException(
          {
            errorCode: 40403,
            errorMessage: 'Article does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }

      throw new InternalServerErrorException();
    }
  }

  // async getNotes(id: number): Promise<NoteResponseTo[]> {
  //   try {
  //     const article = await this.articleRepository.findOne({ where: { id } });
  //     if (!article) throw new ConflictException();
  //     const notes: Note[] = await this.noteRepository.find({
  //       where: { articleId: id },
  //     });
  //     return notes;
  //   } catch (err) {
  //     if (err instanceof ConflictException) {
  //       throw new HttpException(
  //         {
  //           errorCode: 40403,
  //           errorMessage: 'Article does not exist.',
  //         },
  //         HttpStatus.NOT_FOUND,
  //       );
  //     }
  //     throw new InternalServerErrorException();
  //   }
  // }
}

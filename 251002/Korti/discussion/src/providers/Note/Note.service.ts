/* eslint-disable @typescript-eslint/no-unsafe-member-access */
import {
  HttpException,
  HttpStatus,
  Injectable,
  InternalServerErrorException,
} from '@nestjs/common';
import { NoteResponseTo } from './Dto/NoteResponseTo';
import { plainToInstance } from 'class-transformer';
import { Client, mapping } from 'cassandra-driver';
import { Note } from './Note.model';
import { CassandraService } from 'src/cassandra/cassandra.service';
import { NoteRequestTo, UpdateNoteTo } from './Dto/NoteRequestTo';
@Injectable()
export class NoteService {
  private client: Client;
  private noteMapper: mapping.ModelMapper<Note>;

  constructor(cassandraService: CassandraService) {
    this.noteMapper = cassandraService
      .createMapper({
        models: {
          Note: {
            tables: ['tbl_note'],
          },
        },
      })
      .forModel('Note');
    this.client = cassandraService.client;
  }

  async getAllNotes(): Promise<ReadonlyArray<NoteResponseTo>> {
    const notes = await this.client.execute(`SELECT * FROM tbl_note`);
    const res: NoteResponseTo[] = [];
    notes.rows.forEach((el) => {
      const reponseBody = new Note();
      // eslint-disable-next-line @typescript-eslint/no-unsafe-call, @typescript-eslint/no-unsafe-member-access
      for (const key of el.keys()) {
        if (key === 'articleid')
          reponseBody.articleId = parseInt(el[key] as string);
        if (key === 'id') reponseBody.id = parseInt(el[key] as string);
        if (key === 'content') reponseBody.content = el[key] as string;
        if (key === 'country') reponseBody.country = el[key] as string;
      }
      res.push(reponseBody);
    });

    return res;
  }

  async createNote(item: NoteRequestTo): Promise<NoteResponseTo> {
    const note = plainToInstance(Note, item);
    try {
      note.country = 'Belarus';
      await this.client.execute(
        `INSERT INTO distcomp.tbl_note (id, articleid, content, country) VALUES (?, ?, ?, ?)`,
        [note.id, note.articleId, note.content, note.country],
        { prepare: true },
      );
      const createdNote = await this.getNoteById(note.id);
      return createdNote;
    } catch {
      throw new HttpException(
        {
          errorCode: 40005,
          errorMessage: 'Note already exist.',
        },
        HttpStatus.FORBIDDEN,
      );
    }
  }

  async getNoteById(id: number): Promise<NoteResponseTo> {
    const note = await this.client.execute(
      `SELECT * FROM tbl_note WHERE id = ? ALLOW FILTERING`,
      [id],
      {
        prepare: true,
      },
    );
    if (!note.rows[0]) {
      throw new HttpException(
        {
          errorCode: 40404,
          errorMessage: 'Note does not exist.',
        },
        HttpStatus.NOT_FOUND,
      );
    }
    const reponseBody = new Note();
    for (const key of note.rows[0].keys()) {
      if (key === 'articleid')
        reponseBody.articleId = parseInt(note.rows[0][key] as string);
      if (key === 'id') reponseBody.id = parseInt(note.rows[0][key] as string);
      if (key === 'content') reponseBody.content = note.rows[0][key] as string;
      if (key === 'country') reponseBody.country = note.rows[0][key] as string;
    }
    return plainToInstance(NoteResponseTo, reponseBody, {
      excludeExtraneousValues: true,
    });
  }

  async deleteNote(id: number): Promise<void> {
    try {
      const note = await this.getNoteById(id);
      await this.noteMapper.remove(note);
    } catch {
      throw new HttpException(
        {
          errorCode: 40404,
          errorMessage: 'Note does not exist.',
        },
        HttpStatus.NOT_FOUND,
      );
    }
  }

  async updateNote(body: UpdateNoteTo): Promise<NoteResponseTo> {
    try {
      const note = await this.getNoteById(body.id);
      if (!note) {
        throw new HttpException(
          {
            errorCode: 40404,
            errorMessage: 'Note does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      note.content = body.content;
      note.articleId = body.articleId;
      await this.noteMapper.update(note);
      return await this.getNoteById(body.id);
    } catch {
      throw new InternalServerErrorException();
    }
  }
}

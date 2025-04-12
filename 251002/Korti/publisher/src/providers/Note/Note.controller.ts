import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  ParseIntPipe,
  Post,
  Put,
  Res,
} from '@nestjs/common';
import { NoteService } from './Note.service';
import { Response } from 'express';
import { NoteRequestTo, UpdateNoteTo } from './Dto/NoteRequestTo';

@Controller('api/v1.0/notes')
export class NoteController {
  constructor(private readonly noteService: NoteService) {}

  @Get()
  @HttpCode(200)
  async getAll() {
    return await this.noteService.getAllNotes();
  }

  @Get(':id')
  async getById(@Param('id', ParseIntPipe) id: number, @Res() res: Response) {
    const resBody = await this.noteService.getNoteById(id);
    res.status(HttpStatus.OK).json(resBody);
  }

  @Post()
  async createNote(@Body() body: NoteRequestTo, @Res() res: Response) {
    const resBody = await this.noteService.createNote(body);
    res.status(HttpStatus.CREATED).json(resBody);
  }

  @Delete(':id')
  async delete(@Param('id', ParseIntPipe) id: number, @Res() res: Response) {
    await this.noteService.deleteNote(id);
    res.status(HttpStatus.NO_CONTENT).send();
  }

  @Put()
  async update(@Body() body: UpdateNoteTo, @Res() res: Response) {
    const note = await this.noteService.updateNote(body);
    res.status(HttpStatus.OK).json(note);
  }

  // @HttpCode(200)
  // @Get('articles/:noteId')
  // async getNotesByArticle(@Param('id', ParseIntPipe) id: number) {
  //   const notes: NoteResponseTo[] = await this.noteService.getNotes(id);
  //   return notes;
  // }
}

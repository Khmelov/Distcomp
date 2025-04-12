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
} from '@nestjs/common';
import { NoteService } from './Note.service';
import { NoteRequestTo, UpdateNoteTo } from './Dto/NoteRequestTo';
import { MessagePattern, Payload } from '@nestjs/microservices';
import {
  ADD_NEW_NOTE,
  DELETE_NOTE,
  GET_ALL_NOTES,
  GET_NOTE_BY_ID,
  UPDATE_NOTE,
} from 'src/constants/constants';
import { NoteResponseTo } from './Dto/NoteResponseTo';
import { GetKafkaMessage } from './interfaces/get.kafka.message';
import { DeleteKafkaInterface } from './interfaces/delete.kafka.message';
import { UpdateKafkaMessage } from './interfaces/update.kafka.message';
import { CreateKafkaMessage } from './interfaces/create.kafka.message';
@Controller('api/v1.0/notes')
export class NoteController {
  constructor(private readonly noteService: NoteService) {}

  @MessagePattern(GET_ALL_NOTES)
  async getAll(): Promise<ReadonlyArray<NoteResponseTo>> {
    return await this.noteService.getAllNotes();
  }
  @Get()
  @HttpCode(200)
  async getAllRest() {
    return this.getAll();
  }

  @MessagePattern(GET_NOTE_BY_ID)
  async getById(@Payload() msg: GetKafkaMessage) {
    const resBody = await this.noteService.getNoteById(msg.noteId);
    if (!resBody) {
      return null;
    }
    return JSON.stringify(resBody);
  }
  @Get(':id')
  @HttpCode(200)
  async getByIdRest(@Param('id', ParseIntPipe) id: number) {
    return await this.noteService.getNoteById(id);
  }

  @MessagePattern(ADD_NEW_NOTE)
  async createNote(@Payload() msg: CreateKafkaMessage) {
    return JSON.stringify(await this.noteService.createNote(msg));
  }
  @Post()
  @HttpCode(HttpStatus.CREATED)
  async createNoteRest(@Body() body: NoteRequestTo) {
    return await this.createNote(body);
  }

  @MessagePattern(DELETE_NOTE)
  async delete(@Payload() msg: DeleteKafkaInterface) {
    await this.noteService.deleteNote(msg.noteId);
  }
  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async deleteRest(@Param('id', ParseIntPipe) id: number) {
    await this.delete({ noteId: id });
  }

  @MessagePattern(UPDATE_NOTE)
  async update(@Payload() msg: UpdateKafkaMessage) {
    const note = await this.noteService.updateNote(msg);
    return JSON.stringify(note);
  }
  @Put()
  @HttpCode(HttpStatus.OK)
  async updateRest(@Body() body: UpdateNoteTo) {
    return this.update(body);
  }
}

import {
  Controller,
  Get,
  Post,
  Body,
  Param,
  Delete,
  ParseIntPipe,
  Put,
  UsePipes,
  HttpCode,
} from "@nestjs/common";
import { MessagesService } from "./messages.service";
import { CreateMessageDto } from "./dto/create-message.dto";
import { UpdateMessageDto } from "./dto/update-message.dto";
import { ZodValidationPipe } from "nestjs-zod";
import { MessagePattern, Payload } from "@nestjs/microservices";
import { IKafkaMessage } from "src/interfaces";

@Controller("messages")
@UsePipes(ZodValidationPipe)
export class MessagesController {
  constructor(private readonly messagesService: MessagesService) {}

  @Post()
  create(@Body() createMessageDto: CreateMessageDto) {
    return this.messagesService.create(createMessageDto);
  }

  @Get()
  findAll() {
    return this.messagesService.findAll();
  }

  @Get(":id")
  findOne(@Param("id", ParseIntPipe) id: number) {
    return this.messagesService.findOne(id);
  }

  @Put()
  update(@Body() updateMessageDto: UpdateMessageDto) {
    return this.messagesService.update(updateMessageDto);
  }

  @Delete(":id")
  @HttpCode(204)
  remove(@Param("id", ParseIntPipe) id: number) {
    return this.messagesService.remove(id);
  }
}

import {
  HttpException,
  HttpStatus,
  Inject,
  Injectable,
  OnModuleInit,
} from "@nestjs/common";
import { CreateMessageDto } from "./dto/create-Message.dto";
import { UpdateMessageDto } from "./dto/update-Message.dto";
import { PROVIDERS } from "src/constants";
import { ConsumerService } from "../kafka/consumer.service";
import { partition } from "rxjs";

@Injectable()
export class MessagesService implements OnModuleInit {
  constructor(
    @Inject(PROVIDERS.CASSANDRA) private db: any,
    private consumerService: ConsumerService
  ) {}

  async onModuleInit() {
    await this.consumerService.consume(
      { topics: ["InTopic"] },
      {
        eachMessage: async ({ topic, partition, message }) => {
          console.log({
            value: message.value?.toString(),
            topic: topic.toString(),
            partition: partition.toString(),
          });
        },
      }
    );
  }

  async create(createMessageDto: CreateMessageDto) {
    const { storyId, content } = createMessageDto;
    const id = this.generateId();

    await this.db.execute(
      `INSERT INTO tbl_messages
          (id, "storyId", content) 
        VALUES (?, ?, ?)`,
      [id, storyId.toString(), content]
    );

    return await this.findOne(+id);
  }

  async findAll() {
    const rows = (await this.db.execute(`SELECT * FROM tbl_messages`)).rows;
    return this.transformResult(rows);
  }

  async findOne(id: number) {
    const rows = this.transformResult(
      (
        await this.db.execute(`SELECT * FROM tbl_messages WHERE id = ?`, [
          id.toString(),
        ])
      ).rows
    );

    if (!rows.length) {
      throw new HttpException("Message not found", HttpStatus.NOT_FOUND);
    }

    return rows.length ? rows[0] : null;
  }

  async update(updateMessageDto: UpdateMessageDto) {
    const { id, storyId, content } = updateMessageDto;

    await this.findOne(id);

    await this.db.execute(
      `UPDATE tbl_messages
        SET "storyId" = ?, "content" = ?
        WHERE "id" = ?`,
      [storyId.toString(), content, id.toString()]
    );

    return await this.findOne(id);
  }

  async remove(id: number) {
    await this.findOne(id);

    await this.db.execute(`DELETE FROM tbl_messages WHERE "id" = ?`, [
      id.toString(),
    ]);
  }

  private generateId = () => {
    return Math.round(Math.random() * 100000).toString();
  };

  private transformResult = (rows: any) => {
    return rows.map((row) => {
      return {
        id: parseInt(row.id),
        content: row.content,
        storyId: parseInt(row.storyId),
      };
    });
  };
}

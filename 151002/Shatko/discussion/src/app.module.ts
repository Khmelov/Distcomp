import { Module } from "@nestjs/common";
import { ConfigModule } from "@nestjs/config";
import { MessagesModule } from "./modules/messages/messages.module";
import { KafkaModule } from "./modules/kafka/kafka.module";

@Module({
  imports: [ConfigModule.forRoot(), MessagesModule, KafkaModule],
  controllers: [],
  providers: [],
})
export class AppModule {}

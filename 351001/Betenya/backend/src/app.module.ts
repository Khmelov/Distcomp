import { Module } from '@nestjs/common';
import { V1AppModule } from './api/v1.0/v1.module';
import { RouterModule } from '@nestjs/core';
import { KafkaModule } from './kafka/kafka.module';
import { RedisModule } from './redis/redis.module';

@Module({
  imports: [
    KafkaModule,
    RedisModule,
    V1AppModule,
    RouterModule.register([
      {
        path: 'api',
        children: [
          {
            path: 'v1.0',
            module: V1AppModule,
          },
        ],
      },
    ]),
  ],
})
export class AppModule {}

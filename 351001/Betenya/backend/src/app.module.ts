import { Module } from '@nestjs/common';
import { V1AppModule } from './api/v1.0/v1.module';
import { V2AppModule } from './api/v2.0/v2.module';
import { RouterModule } from '@nestjs/core';
import { KafkaModule } from './kafka/kafka.module';
import { RedisModule } from './redis/redis.module';
import { AuthModule } from './auth/auth.module';

@Module({
  imports: [
    KafkaModule,
    RedisModule,
    AuthModule,
    V1AppModule,
    V2AppModule,
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

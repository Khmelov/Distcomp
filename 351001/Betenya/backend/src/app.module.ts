import { Module } from '@nestjs/common';
import { V1AppModule } from './api/v1.0/v1.module';
import { RouterModule } from '@nestjs/core';

@Module({
  imports: [
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

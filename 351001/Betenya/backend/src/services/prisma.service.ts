import { Injectable, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { PrismaClient } from '../../generated/prisma/client';
import { PrismaPg } from '@prisma/adapter-pg';
import 'dotenv/config';

@Injectable()
export class PrismaService
  extends PrismaClient
  implements OnModuleInit, OnModuleDestroy
{
  constructor() {
    const connectionString = process.env.DATABASE_URL;

    if (!connectionString) {
      throw new Error('DATABASE_URL environment variable is missing');
    }

    const pool = new PrismaPg({ connectionString });
    super({ adapter: pool });
  }

  async onModuleInit() {
    try {
      await this.$connect();
      console.log('✅ Prisma connected to database');
    } catch (error) {
      console.error('❌ Prisma connection error:', error);
    }
  }

  async onModuleDestroy() {
    await this.$disconnect();
  }
}

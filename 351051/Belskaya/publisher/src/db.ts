import { PrismaPg } from '@prisma/adapter-pg';
import 'dotenv/config';
import pg from 'pg';
import { PrismaClient } from './generated/prisma/client';
import { createClient } from 'redis';

// --- Postgres / Prisma ---
const connectionString = process.env.DATABASE_URL;
const pool = new pg.Pool({ connectionString });

const adapter = new PrismaPg(pool, { schema: 'distcomp' });

export const prisma = new PrismaClient({ adapter });

// --- Redis ---
export const redisClient = createClient({
  url: process.env.REDIS_URL || 'redis://localhost:6379',
});

redisClient.on('error', (err) => console.error('[Redis] Client Error', err));

export async function connectRedis() {
  if (!redisClient.isOpen) {
    await redisClient.connect();
    console.log('[publisher] Connected to Redis');
  }
}

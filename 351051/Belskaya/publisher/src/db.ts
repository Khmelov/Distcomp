import { PrismaPg } from '@prisma/adapter-pg';
import 'dotenv/config';
import pg from 'pg';
import { PrismaClient } from './generated/prisma/client';

const connectionString = process.env.DATABASE_URL;
const pool = new pg.Pool({ connectionString });

const adapter = new PrismaPg(pool, { schema: 'distcomp' });

export const prisma = new PrismaClient({ adapter });

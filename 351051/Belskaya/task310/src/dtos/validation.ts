import { z } from 'zod';

export const authorRequestSchema = z.object({
  login: z.string().min(2).max(64),
  password: z.string().min(8).max(128),
  firstname: z.string().min(2).max(64),
  lastname: z.string().min(2).max(64),
});

export const topicRequestSchema = z.object({
  authorId: z.number().int().positive(),
  title: z.string().min(2).max(64),
  content: z.string().min(4).max(2048),
});

export const markerRequestSchema = z.object({
  name: z.string().min(2).max(32),
});

export const messageRequestSchema = z.object({
  topicId: z.number().int().positive(),
  content: z.string().min(2).max(2048),
});

export type AuthorRequestInput = z.infer<typeof authorRequestSchema>;
export type TopicRequestInput = z.infer<typeof topicRequestSchema>;
export type MarkerRequestInput = z.infer<typeof markerRequestSchema>;
export type MessageRequestInput = z.infer<typeof messageRequestSchema>;
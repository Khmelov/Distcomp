import { ZodType } from 'zod';
import { ValidationError } from '../errors/AppError';

export abstract class BaseService {
  protected readonly entityName: string;

  constructor(entityName: string) {
    this.entityName = entityName;
  }

  protected validate<T>(schema: ZodType<T>, data: unknown): T {
    const result = schema.safeParse(data);
    if (!result.success) {
      const message = result.error.issues
        .map((i) => `${i.path.join('.')}: ${i.message}`)
        .join('; ');
      throw new ValidationError(message);
    }
    return result.data;
  }
}

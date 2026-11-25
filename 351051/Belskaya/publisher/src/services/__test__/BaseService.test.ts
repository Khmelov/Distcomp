import { z } from 'zod';
import { ValidationError } from '../../errors/AppError';
import { BaseService } from '../BaseService';

class TestService extends BaseService {
  constructor() {
    super('test');
  }

  public validateData(schema: z.ZodType, data: unknown) {
    return this.validate(schema, data);
  }
}

describe('BaseService', () => {
  let service: TestService;

  beforeEach(() => {
    service = new TestService();
  });

  describe('validate', () => {
    const schema = z.object({
      name: z.string().min(2),
      age: z.number().int().positive(),
    });

    it('should return validated data when input is valid', () => {
      const data = {
        name: 'John',
        age: 25,
      };

      expect(service.validateData(schema, data)).toEqual(data);
    });

    it('should throw ValidationError when input is invalid', () => {
      const data = {
        name: 'J',
        age: 25,
      };

      expect(() => service.validateData(schema, data)).toThrow(ValidationError);
    });

    it('should include field path in validation error message', () => {
      const data = {
        name: 'J',
        age: 25,
      };

      try {
        service.validateData(schema, data);
        fail('Expected ValidationError to be thrown');
      } catch (error) {
        expect(error).toBeInstanceOf(ValidationError);
        expect((error as Error).message).toContain('name');
      }
    });

    it('should include multiple validation errors in message', () => {
      const data = {
        name: 'J',
        age: -1,
      };

      try {
        service.validateData(schema, data);
        fail('Expected ValidationError to be thrown');
      } catch (error) {
        expect(error).toBeInstanceOf(ValidationError);
        expect((error as Error).message).toContain('name');
        expect((error as Error).message).toContain('age');
        expect((error as Error).message).toContain('; ');
      }
    });

    it('should reject data with incorrect types', () => {
      const data = {
        name: 123,
        age: '25',
      };

      expect(() => service.validateData(schema, data)).toThrow(ValidationError);
    });
  });
});

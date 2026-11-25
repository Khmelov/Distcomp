import { NextFunction, Request, Response } from 'express';
import { AppError } from '../../errors/AppError';
import { errorHandler } from '../errorHandler';

describe('errorHandler', () => {
  let req: Partial<Request>;
  let res: Partial<Response>;
  let next: NextFunction;

  beforeEach(() => {
    req = {};

    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
    };

    next = jest.fn();
  });

  it('should handle invalid JSON errors', () => {
    const error = new SyntaxError('Unexpected token') as SyntaxError & {
      body: string;
    };

    error.body = '{invalid json}';

    errorHandler(error, req as Request, res as Response, next);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({
      errorMessage: 'Invalid JSON format in request body',
      errorCode: '40002',
    });
  });

  it('should handle AppError', () => {
    const error = new AppError(403, '40301', 'Test error');

    errorHandler(error, req as Request, res as Response, next);

    expect(res.status).toHaveBeenCalledWith(403);
    expect(res.json).toHaveBeenCalledWith({
      errorMessage: 'Test error',
      errorCode: '40301',
    });
  });

  it('should handle unknown errors', () => {
    const error = new Error('Unexpected error');

    errorHandler(error, req as Request, res as Response, next);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({
      errorMessage: 'Internal server error',
      errorCode: '50001',
    });
  });
});

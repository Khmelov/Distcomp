import { NextFunction, Request, Response } from 'express';
import { AppError } from '../errors/AppError';

export function errorHandler(
  err: Error,
  req: Request,
  res: Response,
  next: NextFunction
): void {
  if (err instanceof SyntaxError && 'body' in err) {
    res.status(400).json({
      errorMessage: 'Invalid JSON format in request body',
      errorCode: '40002',
    });
    return;
  }

  if (err instanceof AppError) {
    res.status(err.statusCode).json({
      errorMessage: err.message,
      errorCode: err.errorCode,
    });
    return;
  }

  res.status(500).json({
    errorMessage: 'Internal server error',
    errorCode: '50001',
  });
}

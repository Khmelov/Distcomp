import { NextFunction, Request, Response } from 'express';
import { AppError } from '../errors/AppError';

export function errorHandler(
  err: Error,
  req: Request,
  res: Response,
  next: NextFunction
): void {
  if (err instanceof AppError) {
    res.status(err.statusCode).json({
      errorMessage: err.message,
      errorCode: err.errorCode,
    });
    return;
  }

  console.error('Unhandled error:', err);
  res.status(500).json({
    errorMessage: 'Internal server error',
    errorCode: '50001',
  });
}
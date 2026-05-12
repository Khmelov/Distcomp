import { ExceptionFilter, Catch, ArgumentsHost, HttpException, HttpStatus } from '@nestjs/common';
import { Response } from 'express';
import { QueryFailedError } from 'typeorm';

export class BusinessException extends Error {
  constructor(
    public readonly errorCode: string,
    public readonly errorMessage: string,
    public readonly statusCode: number,
  ) {
    super(errorMessage);
  }
}

@Catch()
export class AllExceptionsFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();

    let status = HttpStatus.INTERNAL_SERVER_ERROR;
    let errorMessage = 'Internal server error';
    let errorCode = '50000';

    if (exception instanceof QueryFailedError) {
      const driverError = exception.driverError as any;
      if (driverError && driverError.code === '23505') {
        status = HttpStatus.CONFLICT;
        errorMessage = 'Duplicate key violation';
        errorCode = '40901';
        if (driverError.detail) {
          const match = driverError.detail.match(/Key \(([^)]+)\)=/);
          if (match) {
            errorMessage = `Field "${match[1]}" must be unique`;
          }
        }
      }
    }
    
    if (exception instanceof BusinessException) {
      status = exception.statusCode;
      errorMessage = exception.errorMessage;
      errorCode = exception.errorCode;
    } 
    else if (exception instanceof HttpException) {
      status = exception.getStatus();
      const responseBody = exception.getResponse();
      errorMessage = typeof responseBody === 'string' ? responseBody : (responseBody as any).message;
      errorCode = `${status}00`;
    } 
    else if (exception instanceof Error) {
      errorMessage = exception.message;
    }

    response.status(status).json({
      errorCode,
      errorMessage,
      timestamp: new Date().toISOString(),
    });
  }
}
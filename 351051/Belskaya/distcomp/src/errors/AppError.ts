export class AppError extends Error {
  constructor(
    public readonly statusCode: number,
    public readonly errorCode: string,
    message: string
  ) {
    super(message);
    this.name = this.constructor.name;
    Error.captureStackTrace(this, this.constructor);
  }
}

export class NotFoundError extends AppError {
  constructor(message: string, errorCode = '40401') {
    super(404, errorCode, message);
  }
}

export class ValidationError extends AppError {
  constructor(message: string, errorCode = '40001') {
    super(400, errorCode, message);
  }
}

export class ConflictError extends AppError {
  constructor(message: string, errorCode = '40901') {
    super(409, errorCode, message);
  }
}

export class UnprocessableEntityError extends AppError {
  constructor(message: string, errorCode = '42201') {
    super(422, errorCode, message);
  }
}
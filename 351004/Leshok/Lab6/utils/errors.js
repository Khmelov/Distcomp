// utils/errors.js
class AppError extends Error {
  constructor(message, statusCode, errorCode) {
    super(message);
    this.statusCode = statusCode;
    this.errorCode = errorCode; // 5 цифр: первые три = statusCode
  }
}

class ValidationError extends AppError {
  constructor(message, statusCode = 400) {
    super(message, statusCode, `${statusCode}001`);
  }
}

class UnauthorizedError extends AppError {
  constructor(message) {
    super(message, 401, '401001');
  }
}

class ForbiddenError extends AppError {
  constructor(message) {
    super(message, 403, '403001');
  }
}

class NotFoundError extends AppError {
  constructor(message) {
    super(message, 404, '404001');
  }
}

module.exports = {
  AppError,
  ValidationError,
  UnauthorizedError,
  ForbiddenError,
  NotFoundError,
};
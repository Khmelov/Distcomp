// utils/errorHandler.js
const { AppError } = require('./errors');

function errorHandler(err, req, res, next) {
  if (err instanceof AppError) {
    return res.status(err.statusCode).json({
      errorMessage: err.message,
      errorCode: err.errorCode,
    });
  }
  console.error(err);
  res.status(500).json({
    errorMessage: 'Внутренняя ошибка сервера',
    errorCode: '50000',
  });
}

module.exports = errorHandler;
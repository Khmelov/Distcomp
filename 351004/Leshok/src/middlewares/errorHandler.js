const { AppError } = require('../utils/errors');

function errorHandler(err, req, res, next) {
  if (err instanceof AppError) {
    return res.status(err.statusCode).json({
      errorMessage: err.message,
      errorCode: err.errorCode
    });
  }
  console.error(err);
  res.status(500).json({
    errorMessage: 'Internal server error',
    errorCode: 50000
  });
}

module.exports = errorHandler;
const ApiError = require('../utils/apiError');

function errorHandler(err, req, res, next) {
  console.error(err.stack);

  if (err instanceof ApiError) {
    return res.status(err.httpCode).json({
      errorMessage: err.errorMessage,
      errorCode: err.customCode
    });
  }

  if (err.name === 'SequelizeValidationError') {
    const messages = err.errors.map(e => e.message).join(', ');
    return res.status(400).json({
      errorMessage: messages,
      errorCode: '40001'
    });
  }

  if (err.name === 'SequelizeUniqueConstraintError') {
    const messages = err.errors.map(e => e.message).join(', ');
    return res.status(403).json({
      errorMessage: messages,
      errorCode: '40301'
    });
  }

  if (err.name === 'SequelizeForeignKeyConstraintError') {
    return res.status(404).json({
      errorMessage: `Referenced entity not found: ${err.fields?.join(', ') || 'foreign key constraint violation'}`,
      errorCode: '40402'
    });
  }

  if (err.parent && err.parent.code === '23503') {
    return res.status(404).json({
      errorMessage: 'Referenced entity not found',
      errorCode: '40402'
    });
  }

  if (err.message === 'Entity not found') {
    return res.status(404).json({
      errorMessage: 'Resource not found',
      errorCode: '40401'
    });
  }

  if (process.env.NODE_ENV !== 'production') {
    return res.status(500).json({
      errorMessage: err.message,
      errorCode: '50000',
      stack: err.stack
    });
  }

  res.status(500).json({
    errorMessage: 'Internal server error',
    errorCode: '50000'
  });
}

module.exports = errorHandler;
function errorHandler(err, req, res, next) {
  console.error('Error:', err);
  const status = err.status || 500;
  const code = err.code || (status === 500 ? '50000' : `${status}00`);
  const message = err.message || 'Internal Server Error';
  res.status(status).json({ errorMessage: message, errorCode: code });
}

module.exports = errorHandler;
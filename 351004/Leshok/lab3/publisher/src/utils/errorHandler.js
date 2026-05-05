function errorHandler(err, req, res, next) {
  const status = err.status || 500;
  const code = err.code || (status === 500 ? '50000' : `${status}00`);
  res.status(status).json({ errorMessage: err.message, errorCode: code });
}

module.exports = errorHandler;
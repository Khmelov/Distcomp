class ApiError extends Error {
  constructor(httpCode, customCode, message) {
    super(message);
    this.httpCode = httpCode;
    this.customCode = `${httpCode}${customCode}`;
    this.errorMessage = message;
  }
}

module.exports = ApiError;
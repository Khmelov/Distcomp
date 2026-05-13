class AppError extends Error {
    constructor(statusCode, errorCode, message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}

const errorHandler = (err, req, res, next) => {
    const status = err.statusCode || 500;
    const errorCode = err.errorCode || parseInt(`${status}00`);
    
    res.status(status).json({
        errorMessage: err.message || 'Internal Server Error',
        errorCode: errorCode
    });
};

module.exports = { AppError, errorHandler };
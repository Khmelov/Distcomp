const { body, param, validationResult } = require('express-validator');
const { AppError } = require('../utils/errors');

const handleValidationErrors = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    const firstError = errors.array()[0];
    let errorCode = 40000;
    switch (firstError.param) {
      case 'login': errorCode = 40003; break;
      case 'password': errorCode = 40004; break;
      case 'firstname': errorCode = 40005; break;
      case 'lastname': errorCode = 40006; break;
      case 'title': errorCode = 40021; break;
      case 'content': errorCode = 40022; break;
      case 'name': errorCode = 40033; break;
      case 'creatorId': errorCode = 40011; break;
      case 'newsId': errorCode = 40041; break;
      default: errorCode = 40000;
    }
    throw new AppError(firstError.msg, 400, errorCode);
  }
  next();
};

const validateCreator = [
  body('login').isLength({ min: 2, max: 64 }).withMessage('Login must be between 2 and 64 characters'),
  body('password').isLength({ min: 8, max: 128 }).withMessage('Password must be between 8 and 128 characters'),
  body('firstname').isLength({ min: 2, max: 64 }).withMessage('Firstname must be between 2 and 64 characters'),
  body('lastname').isLength({ min: 2, max: 64 }).withMessage('Lastname must be between 2 and 64 characters'),
  handleValidationErrors
];

const validateNews = [
  body('title').isLength({ min: 2, max: 64 }).withMessage('Title must be between 2 and 64 characters'),
  body('content').isLength({ min: 4, max: 2048 }).withMessage('Content must be between 4 and 2048 characters'),
  body('creatorId').isInt().withMessage('creatorId must be an integer'),
  body('stickerIds').optional().isArray().withMessage('stickerIds must be an array'),
  handleValidationErrors
];

const validateSticker = [
  body('name').isLength({ min: 2, max: 32 }).withMessage('Name must be between 2 and 32 characters'),
  handleValidationErrors
];

const validateNote = [
  body('content').isLength({ min: 2, max: 2048 }).withMessage('Content must be between 2 and 2048 characters'),
  body('newsId').isInt().withMessage('newsId must be an integer'),
  handleValidationErrors
];

const validateId = [
  param('id').isInt().withMessage('ID must be an integer'),
  handleValidationErrors
];

module.exports = {
  validateCreator,
  validateNews,
  validateSticker,
  validateNote,
  validateId
};
// middleware/auth.js
const jwt = require('jsonwebtoken');
const { UnauthorizedError, ForbiddenError } = require('../utils/errors');

const JWT_SECRET = process.env.JWT_SECRET;

exports.generateToken = (user) => {
  return jwt.sign(
    {
      sub: user.login,
      role: user.role,
    },
    JWT_SECRET,
    { expiresIn: '1h', issuer: 'task361-api' }
  );
};

exports.verifyToken = (req, res, next) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return next(new UnauthorizedError('Отсутствует или неверный формат токена'));
  }
  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    req.user = decoded; // { sub, role, iat, exp }
    next();
  } catch (err) {
    next(new UnauthorizedError('Недействительный или просроченный токен'));
  }
};

exports.checkRole = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) return next(new UnauthorizedError('Не аутентифицирован'));
    if (allowedRoles.includes(req.user.role)) return next();
    next(new ForbiddenError('Недостаточно прав'));
  };
};
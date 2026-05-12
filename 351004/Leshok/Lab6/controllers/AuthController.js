// controllers/AuthController.js
const { authenticate } = require('../services/AuthService');
const { generateToken } = require('../middleware/auth');
const { UnauthorizedError } = require('../utils/errors');

exports.login = async (req, res, next) => {
  try {
    const { login, password } = req.body;
    const user = await authenticate(login, password);
    if (!user) throw new UnauthorizedError('Неверный логин или пароль');
    const token = generateToken(user);
    res.json({
      access_token: token,
      token_type: 'Bearer',
      expires_in: 3600,
    });
  } catch (err) { next(err); }
};
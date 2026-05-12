// services/AuthService.js
const creatorRepo = require('../repositories/CreatorRepository');
const bcrypt = require('bcrypt');

async function authenticate(login, password) {
  const creator = creatorRepo.findByLogin(login);
  if (!creator) return null;
  const match = await bcrypt.compare(password, creator.password);
  if (!match) return null;
  return { login: creator.login, role: creator.role };
}

module.exports = { authenticate };
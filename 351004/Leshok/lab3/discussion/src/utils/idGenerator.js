function generateId() {
  return Number(Date.now() * 10000 + Math.floor(Math.random() * 10000));
}

module.exports = { generateId };
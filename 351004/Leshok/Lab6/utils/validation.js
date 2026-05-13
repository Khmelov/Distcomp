// utils/validation.js
function isValidId(id) {
  return !isNaN(Number(id)) && Number(id) > 0;
}

module.exports = { isValidId };
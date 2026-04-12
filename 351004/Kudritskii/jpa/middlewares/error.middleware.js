module.exports = (err, req, res, next) => {
  if (err.name === "SequelizeUniqueConstraintError") {
    return res.sendStatus(403);
  }

  if (err.name === "SequelizeValidationError") {
    return res.sendStatus(400);
  }

  console.error(err);
  return res.sendStatus(500);
};
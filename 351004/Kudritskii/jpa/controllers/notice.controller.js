const { Notice, User } = require("../models");

exports.create = async (req, res, next) => {
  try {
    const { title, content, UserId } = req.body;

    if (!title || !content || !UserId) return res.sendStatus(400);

    const user = await User.findByPk(UserId);
    if (!user) return res.sendStatus(400);

    const notice = await Notice.create({ title, content, UserId });
    res.status(201).json(notice);
  } catch (err) {
    next(err);
  }
};

exports.findAll = async (req, res) => {
  const notices = await Notice.findAll();
  res.json(notices);
};

exports.findById = async (req, res) => {
  const notice = await Notice.findByPk(req.params.id);
  if (!notice) return res.sendStatus(404);
  res.json(notice);
};

exports.update = async (req, res, next) => {
  try {
    const notice = await Notice.findByPk(req.params.id);
    if (!notice) return res.sendStatus(404);

    const { title, content } = req.body;
    if (!title || !content) return res.sendStatus(400);

    await notice.update({ title, content });
    res.json(notice);
  } catch (err) {
    next(err);
  }
};

exports.delete = async (req, res) => {
  const notice = await Notice.findByPk(req.params.id);
  if (!notice) return res.sendStatus(404);

  await notice.destroy();
  res.sendStatus(204);
};
const { News, User, Label } = require("../models");

exports.create = async (req, res, next) => {
  try {
    const { title, content, userId, labels } = req.body;

    if (!title || !content || !userId) return res.sendStatus(400);

    const user = await User.findByPk(userId);
    if (!user) return res.sendStatus(400);

    const news = await News.create({ title, content, userId });

    if (labels) {
      const existingLabels = await Label.findAll({
        where: { id: labels },
      });

      if (existingLabels.length !== labels.length)
        return res.sendStatus(400);

      await news.setLabels(existingLabels);
    }

    res.status(201).json(news);
  } catch (err) {
    next(err);
  }
};

exports.findAll = async (req, res) => {
  const news = await News.findAll({ include: [{ model: Label }] });
  res.json(news);
};

exports.findById = async (req, res) => {
  const news = await News.findByPk(req.params.id, {
    include: [{ model: Label }],
  });
  if (!news) return res.sendStatus(404);
  res.json(news);
};

exports.update = async (req, res, next) => {
  try {
    const news = await News.findByPk(req.params.id);
    if (!news) return res.sendStatus(404);

    const { title, content } = req.body;
    if (!title || !content) return res.sendStatus(400);

    await news.update({ title, content });
    res.json(news);
  } catch (err) {
    next(err);
  }
};

exports.delete = async (req, res) => {
  const news = await News.findByPk(req.params.id);
  if (!news) return res.sendStatus(404);

  await news.destroy();
  res.sendStatus(204);
};
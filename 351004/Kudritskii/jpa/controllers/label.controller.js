const { Label } = require("../models");

exports.create = async (req, res, next) => {
  try {
    const { name } = req.body;
    if (!name) return res.sendStatus(400);

    const label = await Label.create({ name });
    res.status(201).json(label);
  } catch (err) {
    next(err);
  }
};

exports.findAll = async (req, res) => {
  const labels = await Label.findAll();
  res.json(labels);
};

exports.findById = async (req, res) => {
  const label = await Label.findByPk(req.params.id);
  if (!label) return res.sendStatus(404);
  res.json(label);
};

exports.update = async (req, res, next) => {
  try {
    const label = await Label.findByPk(req.params.id);
    if (!label) return res.sendStatus(404);

    const { name } = req.body;
    if (!name) return res.sendStatus(400);

    await label.update({ name });
    res.json(label);
  } catch (err) {
    next(err);
  }
};

exports.delete = async (req, res) => {
  const label = await Label.findByPk(req.params.id);
  if (!label) return res.sendStatus(404);

  await label.destroy();
  res.sendStatus(204);
};
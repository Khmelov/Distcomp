// controllers/NewsController.js
const newsService = require('../services/NewsService');

exports.getAll = async (req, res, next) => {
  try {
    const all = await newsService.getAll();
    res.json(all);
  } catch (err) { next(err); }
};

exports.getById = async (req, res, next) => {
  try {
    const item = await newsService.getById(req.params.id);
    res.json(item);
  } catch (err) { next(err); }
};

exports.create = async (req, res, next) => {
  try {
    const created = await newsService.create(req.body);
    res.status(201).json(created);
  } catch (err) { next(err); }
};

exports.update = async (req, res, next) => {
  try {
    const updated = await newsService.update(req.params.id, req.body);
    res.json(updated);
  } catch (err) { next(err); }
};

exports.delete = async (req, res, next) => {
  try {
    await newsService.delete(req.params.id);
    res.status(204).send();
  } catch (err) { next(err); }
};
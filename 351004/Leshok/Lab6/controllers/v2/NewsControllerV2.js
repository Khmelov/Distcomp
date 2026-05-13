// controllers/v2/NewsControllerV2.js
const newsService = require('../../services/NewsService');

exports.getAll = async (req, res, next) => {
  try {
    const newsList = await newsService.getAll();
    res.json(newsList);
  } catch (err) { next(err); }
};

exports.getById = async (req, res, next) => {
  try {
    const news = await newsService.getById(req.params.id);
    res.json(news);
  } catch (err) { next(err); }
};

exports.create = async (req, res, next) => {
  try {
    const created = await newsService.create(req.body, req.user.sub);
    res.status(201).json(created);
  } catch (err) { next(err); }
};

exports.update = async (req, res, next) => {
  try {
    const updated = await newsService.update(req.params.id, req.body, req.user.sub);
    res.json(updated);
  } catch (err) { next(err); }
};

exports.delete = async (req, res, next) => {
  try {
    await newsService.delete(req.params.id, req.user.sub);
    res.status(204).send();
  } catch (err) { next(err); }
};
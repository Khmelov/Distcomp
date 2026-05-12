// controllers/v2/CreatorControllerV2.js
const creatorService = require('../../services/CreatorService');

exports.getAll = async (req, res, next) => {
  try {
    const creators = await creatorService.getAll();
    res.json(creators);
  } catch (err) { next(err); }
};

exports.getById = async (req, res, next) => {
  try {
    const creator = await creatorService.getById(req.params.id);
    res.json(creator);
  } catch (err) { next(err); }
};

exports.create = async (req, res, next) => {
  try {
    const newCreator = await creatorService.create(req.body);
    res.status(201).json(newCreator);
  } catch (err) { next(err); }
};

exports.update = async (req, res, next) => {
  try {
    const updated = await creatorService.update(req.params.id, req.body);
    res.json(updated);
  } catch (err) { next(err); }
};

exports.delete = async (req, res, next) => {
  try {
    await creatorService.delete(req.params.id);
    res.status(204).send();
  } catch (err) { next(err); }
};
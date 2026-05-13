// controllers/v2/NoteControllerV2.js
const noteService = require('../../services/NoteService');

exports.getAll = async (req, res, next) => {
  try {
    const notes = await noteService.getAll();
    res.json(notes);
  } catch (err) { next(err); }
};

exports.getById = async (req, res, next) => {
  try {
    const note = await noteService.getById(req.params.id);
    res.json(note);
  } catch (err) { next(err); }
};

exports.create = async (req, res, next) => {
  try {
    const note = await noteService.create(req.body, req.user.sub);
    res.status(201).json(note);
  } catch (err) { next(err); }
};

exports.update = async (req, res, next) => {
  try {
    const updated = await noteService.update(req.params.id, req.body, req.user.sub);
    res.json(updated);
  } catch (err) { next(err); }
};

exports.delete = async (req, res, next) => {
  try {
    await noteService.delete(req.params.id, req.user.sub);
    res.status(204).send();
  } catch (err) { next(err); }
};
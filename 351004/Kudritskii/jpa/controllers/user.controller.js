const BaseService = require("../services/base.service");
const { User } = require("../models");

const service = new BaseService(User);

exports.create = async (req, res) => {
  const data = await service.create(req.body);
  res.status(201).json(data);
};

exports.getById = async (req, res) => {
  const data = await service.findById(req.params.id);
  res.json(data);
};

exports.getAll = async (req, res) => {
  const result = await service.findAll(req.query);
  res.json(result);
};

exports.update = async (req, res) => {
  const data = await service.update(req.params.id, req.body);
  res.json(data);
};

exports.delete = async (req, res) => {
  await service.delete(req.params.id);
  res.status(204).send();
};

const newsService = require('../services/news.service');
const { AppError } = require('../utils/errors');

class NewsController {
  async getAll(req, res, next) {
    try {
      const newsList = newsService.getAll();
      res.json(newsList);
    } catch (err) {
      next(err);
    }
  }

  async getById(req, res, next) {
    try {
      const { id } = req.params;
      const news = newsService.getById(id);
      res.json(news);
    } catch (err) {
      next(err);
    }
  }

  async create(req, res, next) {
    try {
      const data = req.body;
      const newNews = newsService.create(data);
      res.status(201).json(newNews);
    } catch (err) {
      next(err);
    }
  }

  async update(req, res, next) {
    try {
      const { id } = req.params;
      const data = req.body;
      if (data.id) delete data.id;
      const updated = newsService.update(id, data);
      res.json(updated);
    } catch (err) {
      next(err);
    }
  }

  async updateWithIdInBody(req, res, next) {
    try {
      const data = req.body;
      if (!data.id) {
        throw new AppError('Missing id in request body', 400, 40020);
      }
      const id = data.id;
      delete data.id;
      const updated = newsService.update(id, data);
      res.json(updated);
    } catch (err) {
      next(err);
    }
  }

  async delete(req, res, next) {
    try {
      const { id } = req.params;
      newsService.delete(id);
      res.status(204).send();
    } catch (err) {
      next(err);
    }
  }

  async getCreatorByNewsId(req, res, next) {
    try {
      const { newsId } = req.params;
      const creator = newsService.getCreatorByNewsId(newsId);
      res.json(creator);
    } catch (err) {
      next(err);
    }
  }

  async getStickersByNewsId(req, res, next) {
    try {
      const { newsId } = req.params;
      const stickers = newsService.getStickersByNewsId(newsId);
      res.json(stickers);
    } catch (err) {
      next(err);
    }
  }

  async getNotesByNewsId(req, res, next) {
    try {
      const { newsId } = req.params;
      const notes = newsService.getNotesByNewsId(newsId);
      res.json(notes);
    } catch (err) {
      next(err);
    }
  }

  async searchNews(req, res, next) {
    try {
      const params = req.query;
      const newsList = newsService.searchNews(params);
      res.json(newsList);
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new NewsController();
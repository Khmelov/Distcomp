const newsRepo = require('../repositories/news.repository');
const creatorRepo = require('../repositories/creator.repository');
const stickerRepo = require('../repositories/sticker.repository');
const noteRepo = require('../repositories/note.repository');
const { AppError } = require('../utils/errors');
const { mapToNewsResponse, mapToNewsEntity } = require('../dtos/news.dto');

class NewsService {
  getAll() {
    const newsList = newsRepo.findAll();
    return newsList.map(mapToNewsResponse);
  }

  getById(id) {
    const news = newsRepo.findById(Number(id));
    if (!news) {
      throw new AppError('News not found', 404, 40410);
    }
    return mapToNewsResponse(news);
  }

  create(data) {
    this.validateNewsData(data);
    const creator = creatorRepo.findById(Number(data.creatorId));
    if (!creator) {
      throw new AppError('Creator not found', 400, 40011);
    }
    if (data.stickerIds && data.stickerIds.length) {
      for (let sid of data.stickerIds) {
        if (!stickerRepo.findById(Number(sid))) {
          throw new AppError(`Sticker with id ${sid} not found`, 400, 40012);
        }
      }
    }
    const newNews = mapToNewsEntity(data);
    newNews.created = new Date().toISOString();
    newNews.modified = new Date().toISOString();
    const created = newsRepo.create(newNews);
    return mapToNewsResponse(created);
  }

  update(id, data) {
    const existing = newsRepo.findById(Number(id));
    if (!existing) {
      throw new AppError('News not found', 404, 40410);
    }
    if (data.creatorId) {
      const creator = creatorRepo.findById(Number(data.creatorId));
      if (!creator) {
        throw new AppError('Creator not found', 400, 40011);
      }
    }
    if (data.stickerIds) {
      for (let sid of data.stickerIds) {
        if (!stickerRepo.findById(Number(sid))) {
          throw new AppError(`Sticker with id ${sid} not found`, 400, 40012);
        }
      }
    }
    this.validateNewsData(data, true);
    const updatedNews = { ...existing, ...data, modified: new Date().toISOString() };
    const result = newsRepo.update(Number(id), updatedNews);
    return mapToNewsResponse(result);
  }

  delete(id) {
    const news = newsRepo.findById(Number(id));
    if (!news) {
      throw new AppError('News not found', 404, 40410);
    }
    const notes = noteRepo.findAll().filter(n => n.newsId === Number(id));
    notes.forEach(n => noteRepo.delete(n.id));
    newsRepo.delete(Number(id));
    return true;
  }

  validateNewsData(data, isUpdate = false) {
    const { title, content } = data;
    if (!isUpdate) {
      if (!title || !content || !data.creatorId) {
        throw new AppError('Missing required fields: title, content, creatorId', 400, 40020);
      }
    }
    if (title && (title.length < 2 || title.length > 64)) {
      throw new AppError('Title must be between 2 and 64 characters', 400, 40021);
    }
    if (content && (content.length < 4 || content.length > 2048)) {
      throw new AppError('Content must be between 4 and 2048 characters', 400, 40022);
    }
  }

  // Optional methods
  getCreatorByNewsId(newsId) {
    const news = this.getById(newsId);
    const creator = creatorRepo.findById(news.creatorId);
    if (!creator) {
      throw new AppError('Creator not found for this news', 404, 40401);
    }
    const { mapToCreatorResponse } = require('../dtos/creator.dto');
    return mapToCreatorResponse(creator);
  }

  getStickersByNewsId(newsId) {
    const news = this.getById(newsId);
    if (!news.stickerIds || news.stickerIds.length === 0) return [];
    const stickers = news.stickerIds.map(sid => stickerRepo.findById(sid)).filter(s => s);
    const { mapToStickerResponse } = require('../dtos/sticker.dto');
    return stickers.map(mapToStickerResponse);
  }

  getNotesByNewsId(newsId) {
    this.getById(newsId);
    const notes = noteRepo.findAll().filter(n => n.newsId === Number(newsId));
    const { mapToNoteResponse } = require('../dtos/note.dto');
    return notes.map(mapToNoteResponse);
  }

  searchNews(params) {
    let newsList = newsRepo.findAll();
    if (params.creatorLogin) {
      const creators = creatorRepo.findAll().filter(c => c.login.includes(params.creatorLogin));
      const creatorIds = creators.map(c => c.id);
      newsList = newsList.filter(n => creatorIds.includes(n.creatorId));
    }
    if (params.title) {
      newsList = newsList.filter(n => n.title.includes(params.title));
    }
    if (params.content) {
      newsList = newsList.filter(n => n.content.includes(params.content));
    }
    if (params.stickerIds) {
      const stickerIdArray = params.stickerIds.split(',').map(Number);
      newsList = newsList.filter(n => n.stickerIds && stickerIdArray.some(sid => n.stickerIds.includes(sid)));
    }
    if (params.stickerNames) {
      const stickerNameArray = params.stickerNames.split(',');
      const stickers = stickerRepo.findAll().filter(s => stickerNameArray.includes(s.name));
      const stickerIds = stickers.map(s => s.id);
      newsList = newsList.filter(n => n.stickerIds && stickerIds.some(sid => n.stickerIds.includes(sid)));
    }
    return newsList.map(mapToNewsResponse);
  }
}

module.exports = new NewsService();
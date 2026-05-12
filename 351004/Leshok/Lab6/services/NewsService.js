// services/NewsService.js
const newsRepo = require('../repositories/NewsRepository');
const creatorRepo = require('../repositories/CreatorRepository');
const News = require('../models/News');
const { toResponse, fromRequest } = require('../dto/newsDto');
const { ValidationError, NotFoundError } = require('../utils/errors');

class NewsService {
  async getAll() {
    return newsRepo.findAll().map(toResponse);
  }

  async getById(id) {
    const news = newsRepo.findById(id);
    if (!news) throw new NotFoundError('News не найдена');
    return toResponse(news);
  }

  async create(newsRequest, currentUserLogin = null) {
    // Проверка, что creator существует
    const { title, content, creatorId } = fromRequest(newsRequest);
    const creator = creatorRepo.findById(creatorId);
    if (!creator) throw new ValidationError('Creator с таким id не существует', 400);
    
    // Если передан currentUserLogin (для v2), проверяем, что пользователь владелец или ADMIN
    if (currentUserLogin && creator.login !== currentUserLogin) {
      const user = creatorRepo.findByLogin(currentUserLogin);
      if (user.role !== 'ADMIN') throw new ValidationError('Вы можете создавать новости только от своего имени', 403);
    }
    
    const news = new News(null, title, content, creatorId);
    const saved = newsRepo.create(news);
    return toResponse(saved);
  }

  async update(id, newsRequest, currentUserLogin = null) {
    const existing = newsRepo.findById(id);
    if (!existing) throw new NotFoundError('News не найдена');
    
    // Проверка прав: только ADMIN или владелец новости
    if (currentUserLogin) {
      const creator = creatorRepo.findById(existing.creatorId);
      if (creator.login !== currentUserLogin) {
        const user = creatorRepo.findByLogin(currentUserLogin);
        if (user.role !== 'ADMIN') throw new ValidationError('Нет прав на редактирование этой новости', 403);
      }
    }
    
    const updateData = {};
    if (newsRequest.title !== undefined) updateData.title = newsRequest.title;
    if (newsRequest.content !== undefined) updateData.content = newsRequest.content;
    if (newsRequest.creatorId !== undefined) {
      const newCreator = creatorRepo.findById(newsRequest.creatorId);
      if (!newCreator) throw new ValidationError('Creator не найден', 400);
      updateData.creatorId = newsRequest.creatorId;
    }
    updateData.modified = new Date().toISOString();
    
    const updated = newsRepo.update(id, updateData);
    return toResponse(updated);
  }

  async delete(id, currentUserLogin = null) {
    const existing = newsRepo.findById(id);
    if (!existing) throw new NotFoundError('News не найдена');
    if (currentUserLogin) {
      const creator = creatorRepo.findById(existing.creatorId);
      if (creator.login !== currentUserLogin) {
        const user = creatorRepo.findByLogin(currentUserLogin);
        if (user.role !== 'ADMIN') throw new ValidationError('Нет прав на удаление этой новости', 403);
      }
    }
    return newsRepo.delete(id);
  }
}

module.exports = new NewsService();
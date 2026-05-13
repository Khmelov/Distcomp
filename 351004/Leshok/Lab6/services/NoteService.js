// services/NoteService.js
const noteRepo = require('../repositories/NoteRepository');
const newsRepo = require('../repositories/NewsRepository');
const Note = require('../models/Note');
const { toResponse, fromRequest } = require('../dto/noteDto');
const { ValidationError, NotFoundError } = require('../utils/errors');

class NoteService {
  async getAll() {
    return noteRepo.findAll().map(toResponse);
  }

  async getById(id) {
    const note = noteRepo.findById(id);
    if (!note) throw new NotFoundError('Note не найден');
    return toResponse(note);
  }

  async create(noteRequest, currentUserLogin = null) {
    const { content, newsId } = fromRequest(noteRequest);
    if (!content || content.trim() === '') {
      throw new ValidationError('Content не может быть пустым');
    }
    const news = newsRepo.findById(newsId);
    if (!news) throw new ValidationError('News с таким id не существует', 400);
    
    // Если передан currentUserLogin (v2), проверяем право на создание комментария
    if (currentUserLogin) {
      const creatorRepo = require('../repositories/CreatorRepository');
      const newsCreator = creatorRepo.findById(news.creatorId);
      const user = creatorRepo.findByLogin(currentUserLogin);
      if (newsCreator.login !== currentUserLogin && user.role !== 'ADMIN') {
        throw new ValidationError('Вы можете комментировать только свои новости', 403);
      }
    }
    
    const note = new Note(null, content, newsId);
    const saved = noteRepo.create(note);
    return toResponse(saved);
  }

  async update(id, noteRequest, currentUserLogin = null) {
    const existing = noteRepo.findById(id);
    if (!existing) throw new NotFoundError('Note не найден');
    
    // Проверка прав для v2
    if (currentUserLogin) {
      const news = newsRepo.findById(existing.newsId);
      const creatorRepo = require('../repositories/CreatorRepository');
      const newsCreator = creatorRepo.findById(news.creatorId);
      const user = creatorRepo.findByLogin(currentUserLogin);
      if (newsCreator.login !== currentUserLogin && user.role !== 'ADMIN') {
        throw new ValidationError('Нет прав на редактирование этого комментария', 403);
      }
    }
    
    const updateData = {};
    if (noteRequest.content !== undefined) {
      if (noteRequest.content.trim() === '') throw new ValidationError('Content не может быть пустым');
      updateData.content = noteRequest.content;
    }
    if (noteRequest.newsId !== undefined) {
      const newNews = newsRepo.findById(noteRequest.newsId);
      if (!newNews) throw new ValidationError('News не найден', 400);
      updateData.newsId = noteRequest.newsId;
    }
    
    const updated = noteRepo.update(id, updateData);
    return toResponse(updated);
  }

  async delete(id, currentUserLogin = null) {
    const existing = noteRepo.findById(id);
    if (!existing) throw new NotFoundError('Note не найден');
    
    if (currentUserLogin) {
      const news = newsRepo.findById(existing.newsId);
      const creatorRepo = require('../repositories/CreatorRepository');
      const newsCreator = creatorRepo.findById(news.creatorId);
      const user = creatorRepo.findByLogin(currentUserLogin);
      if (newsCreator.login !== currentUserLogin && user.role !== 'ADMIN') {
        throw new ValidationError('Нет прав на удаление этого комментария', 403);
      }
    }
    
    return noteRepo.delete(id);
  }

  // Дополнительно: получить все комментарии по id новости
  async getByNewsId(newsId) {
    const notes = noteRepo.findByNewsId(newsId);
    return notes.map(toResponse);
  }
}

module.exports = new NoteService();
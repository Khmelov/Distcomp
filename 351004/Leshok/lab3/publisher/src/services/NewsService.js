const { News, Creator, Sticker } = require('../models');
const { NewsResponseDto } = require('../dtos/NewsDto');

class NewsService {
  async create(dto) {
    const { creatorId, title, content } = dto;
    const creator = await Creator.findByPk(creatorId);
    if (!creator) throw { status: 400, code: '40003', message: 'Creator does not exist' };
    const news = await News.create({ creatorId, title, content });
    return new NewsResponseDto(news.id, news.creatorId, news.title, news.content, news.created, news.modified);
  }

  async update(id, dto) {
    const news = await News.findByPk(id);
    if (!news) throw { status: 404, code: '40402', message: 'News not found' };
    const { creatorId, title, content } = dto;
    if (creatorId) {
      const creator = await Creator.findByPk(creatorId);
      if (!creator) throw { status: 400, code: '40003', message: 'Creator does not exist' };
    }
    await news.update({ creatorId, title, content, modified: new Date() });
    return new NewsResponseDto(news.id, news.creatorId, news.title, news.content, news.created, news.modified);
  }

  async getById(id) {
    const news = await News.findByPk(id);
    if (!news) throw { status: 404, code: '40402', message: 'News not found' };
    return new NewsResponseDto(news.id, news.creatorId, news.title, news.content, news.created, news.modified);
  }

  async getAll() {
    const newsList = await News.findAll();
    return newsList.map(n => new NewsResponseDto(n.id, n.creatorId, n.title, n.content, n.created, n.modified));
  }

  async delete(id) {
    const news = await News.findByPk(id);
    if (!news) throw { status: 404, code: '40402', message: 'News not found' };
    await news.destroy();
  }

  // дополнительные методы по заданию
  async getCreatorByNewsId(newsId) {
    const news = await News.findByPk(newsId, { include: Creator });
    if (!news) throw { status: 404, code: '40402', message: 'News not found' };
    const c = news.Creator;
    return { id: c.id, login: c.login, password: c.password, firstname: c.firstname, lastname: c.lastname };
  }

  async getStickersByNewsId(newsId) {
    const news = await News.findByPk(newsId, { include: Sticker });
    if (!news) throw { status: 404, code: '40402', message: 'News not found' };
    return news.Stickers.map(s => ({ id: s.id, name: s.name }));
  }
}

module.exports = new NewsService();
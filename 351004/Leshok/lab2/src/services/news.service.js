const BaseRepository = require('../repositories/base.repository');
const { News, Sticker, Creator } = require('../models');
const { NewsResponseTo } = require('../dtos/news.dto');
const ApiError = require('../utils/apiError');

class NewsService {
  constructor() {
    this.repository = new BaseRepository(News);
  }

  async getAll(page, limit, sort, order, filters) {
    if (page === undefined && limit === undefined) {
      const all = await News.findAll({
        where: filters,
        order: [[sort || 'id', (order || 'ASC').toUpperCase()]]
      });
      return all.map(n => new NewsResponseTo(n.id, n.title, n.content, n.created, n.modified, n.creatorId));
    }
    const result = await this.repository.findAll({ page, limit, sort, order, filters });
    return {
      data: result.data.map(n => new NewsResponseTo(n.id, n.title, n.content, n.created, n.modified, n.creatorId)),
      total: result.total,
      page: result.page,
      totalPages: result.totalPages
    };
  }

  async getById(id) {
    const news = await this.repository.findById(id);
    return new NewsResponseTo(news.id, news.title, news.content, news.created, news.modified, news.creatorId);
  }

  async create(newsRequest) {
    const creator = await Creator.findByPk(newsRequest.creatorId);
    if (!creator) {
      throw new ApiError(404, '02', `Creator with id ${newsRequest.creatorId} not found`);
    }
    const news = await News.create({
      title: newsRequest.title,
      content: newsRequest.content,
      creatorId: newsRequest.creatorId
    });
    if (newsRequest.stickerIds && newsRequest.stickerIds.length) {
      const stickers = await Sticker.findAll({ where: { id: newsRequest.stickerIds } });
      await news.addStickers(stickers);
    }
    return new NewsResponseTo(news.id, news.title, news.content, news.created, news.modified, news.creatorId);
  }

  async update(id, newsRequest) {
    try {
      await this.repository.findById(id);

      if (newsRequest.creatorId) {
        const creator = await Creator.findByPk(newsRequest.creatorId);
        if (!creator) {
          throw new ApiError(404, '02', `Creator with id ${newsRequest.creatorId} not found`);
        }
      }

      await News.update(
        {
          title: newsRequest.title,
          content: newsRequest.content,
          modified: new Date(),
          creatorId: newsRequest.creatorId || undefined 
        },
        { where: { id } }
      );

      const updated = await this.repository.findById(id);

      if (newsRequest.stickerIds !== undefined) {
        const stickers = await Sticker.findAll({ where: { id: newsRequest.stickerIds } });
        await updated.setStickers(stickers);
      }

      return new NewsResponseTo(updated.id, updated.title, updated.content, updated.created, updated.modified, updated.creatorId);
    } catch (error) {
      console.error('Update error (simplified):', error);
      throw error;
    }
  }

  async delete(id) {
    await this.repository.delete(id);
  }
}

module.exports = new NewsService();
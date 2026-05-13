// repositories/NewsRepository.js
const BaseRepository = require('./BaseRepository');
const { news, nextId } = require('../config/db');

class NewsRepository extends BaseRepository {
  constructor() {
    super(news, () => nextId.news++);
  }

  findByCreatorId(creatorId) {
    return this.findAll().filter(n => n.creatorId === Number(creatorId));
  }
}

module.exports = new NewsRepository();
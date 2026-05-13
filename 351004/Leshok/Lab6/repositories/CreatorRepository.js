// repositories/CreatorRepository.js
const BaseRepository = require('./BaseRepository');
const { creators, nextId } = require('../config/db');

class CreatorRepository extends BaseRepository {
  constructor() {
    super(creators, () => nextId.creator++);
  }

  findByLogin(login) {
    for (let creator of this.store.values()) {
      if (creator.login === login) return creator;
    }
    return null;
  }
}

module.exports = new CreatorRepository();
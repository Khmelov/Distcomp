class StickerRequestTo {
  constructor(name) {
    this.name = name;
  }
}

class StickerResponseTo {
  constructor(id, name) {
    this.id = Number(id);
    this.name = name;
  }
}

module.exports = { StickerRequestTo, StickerResponseTo };
class StickerRequestDto {
  constructor(name) {
    this.name = name;
  }
}

class StickerResponseDto {
  constructor(id, name) {
    this.id = Number(id);
    this.name = name;
  }
}

module.exports = { StickerRequestDto, StickerResponseDto };
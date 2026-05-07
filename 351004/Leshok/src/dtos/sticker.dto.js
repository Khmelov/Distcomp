function mapToStickerResponse(sticker) {
  return {
    id: sticker.id,
    name: sticker.name
  };
}

function mapToStickerEntity(request) {
  return {
    name: request.name
  };
}

module.exports = { mapToStickerResponse, mapToStickerEntity };
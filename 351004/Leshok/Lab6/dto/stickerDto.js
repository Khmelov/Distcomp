// dto/stickerDto.js
function toResponse(sticker) {
  return {
    id: sticker.id,
    name: sticker.name,
  };
}

function fromRequest(data) {
  return {
    name: data.name,
  };
}

module.exports = { toResponse, fromRequest };
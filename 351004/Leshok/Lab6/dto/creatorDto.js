// dto/creatorDto.js
function toResponse(creator) {
  return {
    id: creator.id,
    login: creator.login,
    firstname: creator.firstName,
    lastname: creator.lastName,
    role: creator.role,
  };
}

function fromRequest(data) {
  return {
    login: data.login,
    password: data.password,
    firstName: data.firstName || data.firstname,
    lastName: data.lastName || data.lastname,
    role: data.role || 'CUSTOMER',
  };
}

module.exports = { toResponse, fromRequest };
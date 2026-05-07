function mapToCreatorResponse(creator) {
  return {
    id: creator.id,
    login: creator.login,
    firstname: creator.firstname,
    lastname: creator.lastname,
    created: creator.created,
    modified: creator.modified
  };
}

function mapToCreatorEntity(request) {
  return {
    login: request.login,
    password: request.password,
    firstname: request.firstname,
    lastname: request.lastname
  };
}

module.exports = { mapToCreatorResponse, mapToCreatorEntity };
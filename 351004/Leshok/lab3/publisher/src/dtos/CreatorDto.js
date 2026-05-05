class CreatorRequestDto {
  constructor(login, password, firstname, lastname) {
    this.login = login;
    this.password = password;
    this.firstname = firstname;
    this.lastname = lastname;
  }
}

class CreatorResponseDto {
  constructor(id, login, password, firstname, lastname) {
    this.id = Number(id);
    this.login = login;
    this.password = password;
    this.firstname = firstname;
    this.lastname = lastname;
  }
}

module.exports = { CreatorRequestDto, CreatorResponseDto };
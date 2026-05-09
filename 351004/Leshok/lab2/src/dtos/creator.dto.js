class CreatorRequestTo {
  constructor(login, password, firstname, lastname) {
    this.login = login;
    this.password = password;
    this.firstname = firstname;
    this.lastname = lastname;
  }
}

class CreatorResponseTo {
  constructor(id, login, firstname, lastname) {
    this.id = Number(id);       
    this.login = login;
    this.firstname = firstname;
    this.lastname = lastname;
  }
}

module.exports = { CreatorRequestTo, CreatorResponseTo };
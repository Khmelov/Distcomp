// models/Creator.js
class Creator {
  constructor(id, login, password, firstName, lastName, role) {
    this.id = id;
    this.login = login;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role; // 'ADMIN' или 'CUSTOMER'
    this.createdAt = new Date().toISOString();
    this.updatedAt = new Date().toISOString();
  }
}
module.exports = Creator;
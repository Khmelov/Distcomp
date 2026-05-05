const { client } = require('../config/cassandra');

class NoteRepository {
  async save(note) {
    const query = `INSERT INTO tbl_note (country, newsId, id, content) VALUES (?, ?, ?, ?)`;
    await client.execute(query, [
      note.country,
      Number(note.newsId),
      Number(note.id),
      note.content
    ], { prepare: true });
    return note;
  }

  async updateFull(note) {
    const query = `UPDATE tbl_note SET content = ? WHERE country = ? AND newsId = ? AND id = ?`;
    await client.execute(query, [
      note.content,
      note.country,
      Number(note.newsId),
      Number(note.id)
    ], { prepare: true });
    return note;
  }

  
  async findByIdOnly(id) {
    const query = `SELECT * FROM tbl_note WHERE id = ? ALLOW FILTERING`;
    const result = await client.execute(query, [Number(id)], { prepare: true });
    if (result.rows.length === 0) return null;
    const row = result.rows[0];
    return {
      id: Number(row.id),
      newsId: Number(row.newsid),
      country: row.country,
      content: row.content
    };
  }

  async findAll() {
    const result = await client.execute(`SELECT * FROM tbl_note`);
    return result.rows.map(row => ({
      id: Number(row.id),
      newsId: Number(row.newsid),
      country: row.country,
      content: row.content
    }));
  }

  async findByNewsId(country, newsId) {
    const query = `SELECT * FROM tbl_note WHERE country = ? AND newsId = ?`;
    const result = await client.execute(query, [country, Number(newsId)], { prepare: true });
    return result.rows.map(row => ({
      id: Number(row.id),
      newsId: Number(row.newsid),
      country: row.country,
      content: row.content
    }));
  }

  async deleteByIdOnly(id) {
    const note = await this.findByIdOnly(id);
    if (!note) return false;
    const query = `DELETE FROM tbl_note WHERE country = ? AND newsId = ? AND id = ?`;
    await client.execute(query, [note.country, note.newsId, Number(id)], { prepare: true });
    return true;
  }

  async updateByIdOnly(id, content) {
    const note = await this.findByIdOnly(id);
    if (!note) return null;
    const query = `UPDATE tbl_note SET content = ? WHERE country = ? AND newsId = ? AND id = ?`;
    await client.execute(query, [content, note.country, note.newsId, Number(id)], { prepare: true });
    return { ...note, content };
  }
}

module.exports = new NoteRepository();
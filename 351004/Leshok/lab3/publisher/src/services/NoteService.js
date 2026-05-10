const axios = require('axios');

const DISCUSSION_URL = process.env.DISCUSSION_URL || 'http://localhost:24130';

class NoteService {
  async createNote(newsId, content) {
    const response = await axios.post(`${DISCUSSION_URL}/api/v1.0/notes`, 
      { newsId: Number(newsId), content }, 
      { headers: { 'x-country': 'default' } }
    );
    return response.data;
  }

  async updateNote(id, newsId, content) {
    const payload = newsId ? { newsId: Number(newsId), content } : { content };
    const response = await axios.put(`${DISCUSSION_URL}/api/v1.0/notes/${Number(id)}`, payload, {
      headers: { 'x-country': 'default' }
    });
    return response.data;
  }

  async getNote(id) {
    const response = await axios.get(`${DISCUSSION_URL}/api/v1.0/notes/${Number(id)}`, {
      headers: { 'x-country': 'default' }
    });
    return response.data;
  }

  async getAllNotes() {
    const response = await axios.get(`${DISCUSSION_URL}/api/v1.0/notes`, {
      headers: { 'x-country': 'default' }
    });
    return response.data;
  }

  async getNotesByNewsId(newsId) {
    const response = await axios.get(`${DISCUSSION_URL}/api/v1.0/notes`, {
      headers: { 'x-country': 'default' },
      params: { newsId: Number(newsId) }
    });
    return response.data;
  }

  async deleteNote(id, newsId) {
    const params = newsId ? { newsId: Number(newsId) } : {};
    await axios.delete(`${DISCUSSION_URL}/api/v1.0/notes/${Number(id)}`, {
      headers: { 'x-country': 'default' },
      params
    });
  }
}

module.exports = new NoteService();
class Store {
    constructor() {
        this.creators = new Map();   
        this.news = new Map();
        this.stickers = new Map();
        this.creatorIdSeq = 1;
        this.newsIdSeq = 1;
        this.stickerIdSeq = 1;
    }

    createCreator(data) {
        for (const c of this.creators.values()) {
            if (c.login === data.login) {
                return { error: 'duplicate login', code: 40301 };
            }
        }
        const id = this.creatorIdSeq++;
        const creator = { id, ...data };
        this.creators.set(id, creator);
        return creator;
    }

    getCreator(id) {
        return this.creators.get(id);
    }

    getAllCreators() {
        return Array.from(this.creators.values());
    }

    updateCreator(id, data) {
        if (!this.creators.has(id)) return null;
        if (data.login) {
            for (const c of this.creators.values()) {
                if (c.login === data.login && c.id !== id) {
                    return { error: 'duplicate login', code: 40301 };
                }
            }
        }
        const updated = { ...this.creators.get(id), ...data, id };
        this.creators.set(id, updated);
        return updated;
    }

    deleteCreator(id) {
        return this.creators.delete(id);
    }

    createNews(data) {
        const id = this.newsIdSeq++;
        const news = {
            id,
            created: new Date().toISOString(),
            modified: new Date().toISOString(),
            ...data
        };
        this.news.set(id, news);
        return news;
    }

    getNews(id) { return this.news.get(id); }
    getAllNews() { return Array.from(this.news.values()); }

    updateNews(id, data) {
        if (!this.news.has(id)) return null;
        const existing = this.news.get(id);
        const updated = { ...existing, ...data, id, modified: new Date().toISOString() };
        this.news.set(id, updated);
        return updated;
    }

    deleteNews(id) { return this.news.delete(id); }

    createSticker(data) {
        const id = this.stickerIdSeq++;
        const sticker = { id, ...data };
        this.stickers.set(id, sticker);
        return sticker;
    }

    getSticker(id) { return this.stickers.get(id); }
    getAllStickers() { return Array.from(this.stickers.values()); }

    updateSticker(id, data) {
        if (!this.stickers.has(id)) return null;
        const updated = { ...this.stickers.get(id), ...data, id };
        this.stickers.set(id, updated);
        return updated;
    }

    deleteSticker(id) { return this.stickers.delete(id); }
}

module.exports = new Store();
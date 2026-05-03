import type {
  UserRequestTo,
  UserResponseTo,
  ArticleRequestTo,
  ArticleResponseTo,
  StickerRequestTo,
  StickerResponseTo,
  NoticeRequestTo,
  NoticeResponseTo,
  ApiError,
} from '@/app/types';

const BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || 'http://localhost:24110/api';
const API_VERSION = '/v1.0';

function apiUrl(path: string): string {
  return `${BASE_URL}${API_VERSION}${path}`;
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let message = `Error ${res.status}`;
    try {
      const body = await res.json();
      message = body.message || body.error || message;
    } catch {
      // ignore parse errors
    }
    const err: ApiError = { status: res.status, message };
    throw err;
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

async function request<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const res = await fetch(apiUrl(path), {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  return handleResponse<T>(res);
}

// ── Users ────────────────────────────────────────────────────────────────────

export const usersApi = {
  getAll(): Promise<UserResponseTo[]> {
    return request('/users');
  },
  getById(id: number): Promise<UserResponseTo> {
    return request(`/users/${id}`);
  },
  create(data: UserRequestTo): Promise<UserResponseTo> {
    return request('/users', { method: 'POST', body: JSON.stringify(data) });
  },
  update(id: number, data: UserRequestTo): Promise<UserResponseTo> {
    return request(`/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },
  delete(id: number): Promise<void> {
    return request(`/users/${id}`, { method: 'DELETE' });
  },
};

// ── Articles ─────────────────────────────────────────────────────────────────

export const articlesApi = {
  getAll(): Promise<ArticleResponseTo[]> {
    return request('/articles');
  },
  getById(id: number): Promise<ArticleResponseTo> {
    return request(`/articles/${id}`);
  },
  create(data: ArticleRequestTo): Promise<ArticleResponseTo> {
    return request('/articles', { method: 'POST', body: JSON.stringify(data) });
  },
  update(id: number, data: ArticleRequestTo): Promise<ArticleResponseTo> {
    return request(`/articles/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },
  delete(id: number): Promise<void> {
    return request(`/articles/${id}`, { method: 'DELETE' });
  },
};

// ── Stickers ─────────────────────────────────────────────────────────────────

export const stickersApi = {
  getAll(): Promise<StickerResponseTo[]> {
    return request('/stickers');
  },
  getById(id: number): Promise<StickerResponseTo> {
    return request(`/stickers/${id}`);
  },
  create(data: StickerRequestTo): Promise<StickerResponseTo> {
    return request('/stickers', { method: 'POST', body: JSON.stringify(data) });
  },
  update(id: number, data: StickerRequestTo): Promise<StickerResponseTo> {
    return request(`/stickers/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },
  delete(id: number): Promise<void> {
    return request(`/stickers/${id}`, { method: 'DELETE' });
  },
};

// ── Notices ──────────────────────────────────────────────────────────────────

export const noticesApi = {
  getAll(): Promise<NoticeResponseTo[]> {
    return request('/notices');
  },
  getById(id: number): Promise<NoticeResponseTo> {
    return request(`/notices/${id}`);
  },
  create(data: NoticeRequestTo): Promise<NoticeResponseTo> {
    return request('/notices', { method: 'POST', body: JSON.stringify(data) });
  },
  update(id: number, data: NoticeRequestTo): Promise<NoticeResponseTo> {
    return request(`/notices/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },
  delete(id: number): Promise<void> {
    return request(`/notices/${id}`, { method: 'DELETE' });
  },
};

// ── Seed default author ──────────────────────────────────────────────────────

export async function seedDefaultAuthor(): Promise<void> {
  try {
    const users = await usersApi.getAll();
    const exists = users.some(
      (u) => u.login === 'betenya.kostya@mail.ru',
    );
    if (!exists) {
      await usersApi.create({
        login: 'betenya.kostya@mail.ru',
        password: 'password123',
        firstname: 'Константин',
        lastname: 'Бетеня',
      });
    }
  } catch {
    // seed silently fails if backend is unavailable
  }
}

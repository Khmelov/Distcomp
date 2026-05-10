// ── User DTOs ────────────────────────────────────────────────────────────────

export interface UserRequestTo {
  login: string;
  password: string;
  firstname: string;
  lastname: string;
}

export interface UserResponseTo {
  id: number;
  login: string;
  firstname: string | null;
  lastname: string | null;
}

// ── Article DTOs ─────────────────────────────────────────────────────────────

export interface ArticleRequestTo {
  userId: number;
  title: string;
  content: string;
  stickers?: string[];
}

export interface ArticleResponseTo {
  id: number;
  userId: number;
  title: string;
  content: string;
  created: string;
  modified: string;
}

// ── Sticker DTOs ─────────────────────────────────────────────────────────────

export interface StickerRequestTo {
  name: string;
}

export interface StickerResponseTo {
  id: number;
  name: string;
}

// ── Notice DTOs ──────────────────────────────────────────────────────────────

export interface NoticeRequestTo {
  content: string;
  articleId: number;
}

export interface NoticeResponseTo {
  id: number;
  content: string;
  articleId: number;
  state: 'PENDING' | 'APPROVE' | 'DECLINE';
}

// ── API Error ────────────────────────────────────────────────────────────────

export interface ApiError {
  status: number;
  message: string;
}

// ── Pagination ───────────────────────────────────────────────────────────────

export interface PaginationParams {
  page: number;
  size: number;
  sort?: string;
  order?: 'asc' | 'desc';
}

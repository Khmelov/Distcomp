'use client';

import { useEffect, useState, useCallback, useMemo } from 'react';
import { Plus, Pencil, Trash2, Search, Eye } from 'lucide-react';
import Link from 'next/link';
import type {
  ArticleResponseTo,
  ArticleRequestTo,
  UserResponseTo,
  StickerResponseTo,
  ApiError,
} from '@/app/types';
import { articlesApi, usersApi, stickersApi } from '@/app/services/api';
import { Modal } from '@/app/components/Modal';
import { ErrorAlert } from '@/app/components/ErrorAlert';
import { ConfirmDialog } from '@/app/components/ConfirmDialog';
import { Pagination } from '@/app/components/Pagination';

const PAGE_SIZE = 10;

function formatISO(date: string): string {
  const d = new Date(date);
  if (isNaN(d.getTime())) return String(date);
  return d.toISOString();
}

export default function ArticlesPage() {
  const [articles, setArticles] = useState<ArticleResponseTo[]>([]);
  const [users, setUsers] = useState<UserResponseTo[]>([]);
  const [allStickers, setAllStickers] = useState<StickerResponseTo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingArticle, setEditingArticle] =
    useState<ArticleResponseTo | null>(null);
  const [form, setForm] = useState<ArticleRequestTo>({
    userId: 0,
    title: '',
    content: '',
    stickers: [],
  });
  const [stickerInput, setStickerInput] = useState('');
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState<ArticleResponseTo | null>(
    null,
  );
  const [deleting, setDeleting] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const [arts, usrs, stks] = await Promise.all([
        articlesApi.getAll(),
        usersApi.getAll(),
        stickersApi.getAll(),
      ]);
      setArticles(arts);
      setUsers(usrs);
      setAllStickers(stks);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const usersMap = useMemo(() => {
    const map = new Map<number, UserResponseTo>();
    for (const u of users) map.set(u.id, u);
    return map;
  }, [users]);

  const filtered = useMemo(() => {
    if (!search.trim()) return articles;
    const q = search.toLowerCase();
    return articles.filter(
      (a) =>
        a.title.toLowerCase().includes(q) ||
        a.content.toLowerCase().includes(q),
    );
  }, [articles, search]);

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  function openCreate() {
    setEditingArticle(null);
    setForm({
      userId: users.length > 0 ? users[0].id : 0,
      title: '',
      content: '',
      stickers: [],
    });
    setStickerInput('');
    setFormErrors({});
    setModalOpen(true);
  }

  function openEdit(article: ArticleResponseTo) {
    setEditingArticle(article);
    setForm({
      userId: article.userId,
      title: article.title,
      content: article.content,
      stickers: [],
    });
    setStickerInput('');
    setFormErrors({});
    setModalOpen(true);
  }

  function validateForm(): Record<string, string> {
    const errors: Record<string, string> = {};
    if (!form.userId) errors.userId = 'Please select a user';
    if (form.title.length < 2 || form.title.length > 64)
      errors.title = 'Title must be 2-64 characters';
    if (form.content.length < 4 || form.content.length > 2048)
      errors.content = 'Content must be 4-2048 characters';
    return errors;
  }

  async function handleSubmit() {
    const errors = validateForm();
    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }
    try {
      setSaving(true);
      setError('');
      const payload: ArticleRequestTo = {
        ...form,
        stickers: form.stickers && form.stickers.length > 0 ? form.stickers : undefined,
      };
      if (editingArticle) {
        await articlesApi.update(editingArticle.id, payload);
      } else {
        await articlesApi.create(payload);
      }
      setModalOpen(false);
      await fetchData();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to save article');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!deleteTarget) return;
    try {
      setDeleting(true);
      setError('');
      await articlesApi.delete(deleteTarget.id);
      setDeleteTarget(null);
      await fetchData();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to delete article');
    } finally {
      setDeleting(false);
    }
  }

  function addSticker() {
    const name = stickerInput.trim();
    if (!name) return;
    if (form.stickers?.includes(name)) return;
    setForm((prev) => ({ ...prev, stickers: [...(prev.stickers || []), name] }));
    setStickerInput('');
  }

  function removeSticker(name: string) {
    setForm((prev) => ({
      ...prev,
      stickers: (prev.stickers || []).filter((s) => s !== name),
    }));
  }

  return (
    <div className="p-8 max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold text-zinc-900 dark:text-zinc-50">
          Articles
        </h1>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-lg bg-zinc-900 text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 transition-colors"
        >
          <Plus size={16} />
          New Article
        </button>
      </div>

      {error && <ErrorAlert message={error} onClose={() => setError('')} />}

      <div className="relative mt-4 mb-4">
        <Search
          size={16}
          className="absolute left-3 top-1/2 -translate-y-1/2 text-zinc-400"
        />
        <input
          type="text"
          placeholder="Search articles..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(1);
          }}
          className="w-full pl-9 pr-4 py-2 text-sm rounded-lg border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-400 dark:focus:ring-zinc-600"
        />
      </div>

      {loading ? (
        <p className="text-sm text-zinc-500 py-8 text-center">Loading...</p>
      ) : filtered.length === 0 ? (
        <p className="text-sm text-zinc-500 py-8 text-center">
          No articles found
        </p>
      ) : (
        <>
          <div className="border border-zinc-200 dark:border-zinc-800 rounded-xl overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-zinc-50 dark:bg-zinc-900 text-left text-zinc-500 dark:text-zinc-400">
                  <th className="px-4 py-3 font-medium">ID</th>
                  <th className="px-4 py-3 font-medium">Title</th>
                  <th className="px-4 py-3 font-medium">Author</th>
                  <th className="px-4 py-3 font-medium">Created</th>
                  <th className="px-4 py-3 font-medium">Modified</th>
                  <th className="px-4 py-3 font-medium w-28">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100 dark:divide-zinc-800">
                {paginated.map((article) => {
                  const author = usersMap.get(article.userId);
                  return (
                    <tr
                      key={article.id}
                      className="hover:bg-zinc-50 dark:hover:bg-zinc-900/50 transition-colors"
                    >
                      <td className="px-4 py-3 text-zinc-600 dark:text-zinc-400 font-mono text-xs">
                        {article.id}
                      </td>
                      <td className="px-4 py-3 text-zinc-900 dark:text-zinc-100 font-medium">
                        {article.title}
                      </td>
                      <td className="px-4 py-3 text-zinc-600 dark:text-zinc-400">
                        {author
                          ? `${author.firstname ?? ''} ${author.lastname ?? ''}`.trim() ||
                            author.login
                          : `User #${article.userId}`}
                      </td>
                      <td className="px-4 py-3 text-zinc-500 dark:text-zinc-500 font-mono text-xs">
                        {formatISO(article.created)}
                      </td>
                      <td className="px-4 py-3 text-zinc-500 dark:text-zinc-500 font-mono text-xs">
                        {formatISO(article.modified)}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-1">
                          <Link
                            href={`/articles/${article.id}`}
                            className="p-1.5 rounded-md text-zinc-400 hover:text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-950 transition-colors"
                            title="View details & notices"
                          >
                            <Eye size={15} />
                          </Link>
                          <button
                            onClick={() => openEdit(article)}
                            className="p-1.5 rounded-md text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 dark:hover:bg-zinc-800 dark:hover:text-zinc-200 transition-colors"
                            title="Edit"
                          >
                            <Pencil size={15} />
                          </button>
                          <button
                            onClick={() => setDeleteTarget(article)}
                            className="p-1.5 rounded-md text-zinc-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950 transition-colors"
                            title="Delete"
                          >
                            <Trash2 size={15} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
          <Pagination
            page={page}
            totalPages={totalPages}
            onPageChange={setPage}
          />
        </>
      )}

      {/* Create / Edit Modal */}
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingArticle ? 'Edit Article' : 'New Article'}
      >
        <div className="space-y-4">
          {/* User select */}
          <div>
            <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
              Author
            </label>
            <select
              value={form.userId}
              onChange={(e) => {
                setForm((prev) => ({ ...prev, userId: Number(e.target.value) }));
                setFormErrors((prev) => {
                  const next = { ...prev };
                  delete next.userId;
                  return next;
                });
              }}
              className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 focus:outline-none focus:ring-2 transition-colors ${
                formErrors.userId
                  ? 'border-red-300 focus:ring-red-400 dark:border-red-700'
                  : 'border-zinc-200 dark:border-zinc-700 focus:ring-zinc-400 dark:focus:ring-zinc-600'
              }`}
            >
              <option value={0} disabled>
                Select author...
              </option>
              {users.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.firstname ?? ''} {u.lastname ?? ''} ({u.login})
                </option>
              ))}
            </select>
            {formErrors.userId && (
              <p className="mt-1 text-xs text-red-600 dark:text-red-400">
                {formErrors.userId}
              </p>
            )}
          </div>

          {/* Title */}
          <div>
            <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
              Title
            </label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => {
                setForm((prev) => ({ ...prev, title: e.target.value }));
                setFormErrors((prev) => {
                  const next = { ...prev };
                  delete next.title;
                  return next;
                });
              }}
              placeholder="Article title"
              className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 placeholder-zinc-400 focus:outline-none focus:ring-2 transition-colors ${
                formErrors.title
                  ? 'border-red-300 focus:ring-red-400 dark:border-red-700'
                  : 'border-zinc-200 dark:border-zinc-700 focus:ring-zinc-400 dark:focus:ring-zinc-600'
              }`}
            />
            {formErrors.title && (
              <p className="mt-1 text-xs text-red-600 dark:text-red-400">
                {formErrors.title}
              </p>
            )}
          </div>

          {/* Content */}
          <div>
            <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
              Content
            </label>
            <textarea
              value={form.content}
              onChange={(e) => {
                setForm((prev) => ({ ...prev, content: e.target.value }));
                setFormErrors((prev) => {
                  const next = { ...prev };
                  delete next.content;
                  return next;
                });
              }}
              rows={4}
              placeholder="Article content..."
              className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 placeholder-zinc-400 focus:outline-none focus:ring-2 transition-colors resize-y ${
                formErrors.content
                  ? 'border-red-300 focus:ring-red-400 dark:border-red-700'
                  : 'border-zinc-200 dark:border-zinc-700 focus:ring-zinc-400 dark:focus:ring-zinc-600'
              }`}
            />
            {formErrors.content && (
              <p className="mt-1 text-xs text-red-600 dark:text-red-400">
                {formErrors.content}
              </p>
            )}
          </div>

          {/* Stickers */}
          <div>
            <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
              Stickers
            </label>
            <div className="flex gap-2">
              <input
                type="text"
                value={stickerInput}
                onChange={(e) => setStickerInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    addSticker();
                  }
                }}
                list="sticker-suggestions"
                placeholder="Type sticker name and press Enter"
                className="flex-1 px-3 py-2 text-sm rounded-lg border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-400 dark:focus:ring-zinc-600"
              />
              <datalist id="sticker-suggestions">
                {allStickers.map((s) => (
                  <option key={s.id} value={s.name} />
                ))}
              </datalist>
              <button
                type="button"
                onClick={addSticker}
                className="px-3 py-2 text-sm font-medium rounded-lg border border-zinc-200 dark:border-zinc-700 text-zinc-700 dark:text-zinc-300 hover:bg-zinc-50 dark:hover:bg-zinc-800 transition-colors"
              >
                Add
              </button>
            </div>
            {(form.stickers?.length ?? 0) > 0 && (
              <div className="flex flex-wrap gap-2 mt-2">
                {form.stickers!.map((s) => (
                  <span
                    key={s}
                    className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium bg-zinc-100 text-zinc-700 dark:bg-zinc-800 dark:text-zinc-300"
                  >
                    {s}
                    <button
                      onClick={() => removeSticker(s)}
                      className="ml-0.5 text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200"
                    >
                      &times;
                    </button>
                  </span>
                ))}
              </div>
            )}
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <button
              onClick={() => setModalOpen(false)}
              className="px-4 py-2 text-sm font-medium rounded-lg border border-zinc-200 dark:border-zinc-700 text-zinc-700 dark:text-zinc-300 hover:bg-zinc-50 dark:hover:bg-zinc-800 transition-colors"
            >
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              disabled={saving}
              className="px-4 py-2 text-sm font-medium rounded-lg bg-zinc-900 text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 transition-colors disabled:opacity-50"
            >
              {saving
                ? 'Saving...'
                : editingArticle
                  ? 'Update'
                  : 'Create'}
            </button>
          </div>
        </div>
      </Modal>

      {/* Delete Confirm */}
      <ConfirmDialog
        open={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Article"
        message={`Are you sure you want to delete "${deleteTarget?.title}"? All related notices will also be affected.`}
        loading={deleting}
      />
    </div>
  );
}

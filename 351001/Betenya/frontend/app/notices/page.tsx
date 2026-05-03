'use client';

import { useEffect, useState, useCallback, useMemo } from 'react';
import { Plus, Pencil, Trash2, Search } from 'lucide-react';
import Link from 'next/link';
import type {
  NoticeResponseTo,
  NoticeRequestTo,
  ArticleResponseTo,
  ApiError,
} from '@/app/types';
import { noticesApi, articlesApi } from '@/app/services/api';
import { Modal } from '@/app/components/Modal';
import { ErrorAlert } from '@/app/components/ErrorAlert';
import { ConfirmDialog } from '@/app/components/ConfirmDialog';
import { Pagination } from '@/app/components/Pagination';

const PAGE_SIZE = 10;

function stateColor(state: string): string {
  switch (state) {
    case 'APPROVE':
      return 'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300';
    case 'DECLINE':
      return 'bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300';
    default:
      return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300';
  }
}

export default function NoticesPage() {
  const [notices, setNotices] = useState<NoticeResponseTo[]>([]);
  const [articles, setArticles] = useState<ArticleResponseTo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingNotice, setEditingNotice] = useState<NoticeResponseTo | null>(
    null,
  );
  const [form, setForm] = useState<NoticeRequestTo>({
    content: '',
    articleId: 0,
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState<NoticeResponseTo | null>(
    null,
  );
  const [deleting, setDeleting] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const [nots, arts] = await Promise.all([
        noticesApi.getAll(),
        articlesApi.getAll(),
      ]);
      setNotices(nots);
      setArticles(arts);
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

  const articlesMap = useMemo(() => {
    const map = new Map<number, ArticleResponseTo>();
    for (const a of articles) map.set(a.id, a);
    return map;
  }, [articles]);

  const filtered = useMemo(() => {
    if (!search.trim()) return notices;
    const q = search.toLowerCase();
    return notices.filter((n) => {
      const art = articlesMap.get(n.articleId);
      return (
        n.content.toLowerCase().includes(q) ||
        n.state.toLowerCase().includes(q) ||
        (art?.title.toLowerCase().includes(q) ?? false)
      );
    });
  }, [notices, search, articlesMap]);

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  function openCreate() {
    setEditingNotice(null);
    setForm({
      content: '',
      articleId: articles.length > 0 ? articles[0].id : 0,
    });
    setFormErrors({});
    setModalOpen(true);
  }

  function openEdit(notice: NoticeResponseTo) {
    setEditingNotice(notice);
    setForm({ content: notice.content, articleId: notice.articleId });
    setFormErrors({});
    setModalOpen(true);
  }

  function validateForm(): Record<string, string> {
    const errors: Record<string, string> = {};
    if (!form.articleId) errors.articleId = 'Please select an article';
    if (form.content.length < 2 || form.content.length > 2048)
      errors.content = 'Content must be 2-2048 characters';
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
      if (editingNotice) {
        await noticesApi.update(editingNotice.id, form);
      } else {
        await noticesApi.create(form);
      }
      setModalOpen(false);
      await fetchData();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to save notice');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!deleteTarget) return;
    try {
      setDeleting(true);
      setError('');
      await noticesApi.delete(deleteTarget.id);
      setDeleteTarget(null);
      await fetchData();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to delete notice');
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div className="p-8 max-w-5xl">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold text-zinc-900 dark:text-zinc-50">
          Notices
        </h1>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-lg bg-zinc-900 text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 transition-colors"
        >
          <Plus size={16} />
          New Notice
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
          placeholder="Search notices..."
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
          No notices found
        </p>
      ) : (
        <>
          <div className="border border-zinc-200 dark:border-zinc-800 rounded-xl overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-zinc-50 dark:bg-zinc-900 text-left text-zinc-500 dark:text-zinc-400">
                  <th className="px-4 py-3 font-medium">ID</th>
                  <th className="px-4 py-3 font-medium">Content</th>
                  <th className="px-4 py-3 font-medium">Article</th>
                  <th className="px-4 py-3 font-medium">State</th>
                  <th className="px-4 py-3 font-medium w-24">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100 dark:divide-zinc-800">
                {paginated.map((notice) => {
                  const art = articlesMap.get(notice.articleId);
                  return (
                    <tr
                      key={notice.id}
                      className="hover:bg-zinc-50 dark:hover:bg-zinc-900/50 transition-colors"
                    >
                      <td className="px-4 py-3 text-zinc-600 dark:text-zinc-400 font-mono text-xs">
                        {notice.id}
                      </td>
                      <td className="px-4 py-3 text-zinc-900 dark:text-zinc-100 max-w-xs truncate">
                        {notice.content}
                      </td>
                      <td className="px-4 py-3 text-zinc-600 dark:text-zinc-400">
                        {art ? (
                          <Link
                            href={`/articles/${art.id}`}
                            className="text-blue-600 hover:underline dark:text-blue-400"
                          >
                            {art.title}
                          </Link>
                        ) : (
                          `#${notice.articleId}`
                        )}
                      </td>
                      <td className="px-4 py-3">
                        <span
                          className={`text-xs font-medium px-2 py-0.5 rounded-full ${stateColor(notice.state)}`}
                        >
                          {notice.state}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => openEdit(notice)}
                            className="p-1.5 rounded-md text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 dark:hover:bg-zinc-800 dark:hover:text-zinc-200 transition-colors"
                            title="Edit"
                          >
                            <Pencil size={15} />
                          </button>
                          <button
                            onClick={() => setDeleteTarget(notice)}
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
        title={editingNotice ? 'Edit Notice' : 'New Notice'}
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
              Article
            </label>
            <select
              value={form.articleId}
              onChange={(e) => {
                setForm((prev) => ({
                  ...prev,
                  articleId: Number(e.target.value),
                }));
                setFormErrors((prev) => {
                  const next = { ...prev };
                  delete next.articleId;
                  return next;
                });
              }}
              className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 focus:outline-none focus:ring-2 transition-colors ${
                formErrors.articleId
                  ? 'border-red-300 focus:ring-red-400 dark:border-red-700'
                  : 'border-zinc-200 dark:border-zinc-700 focus:ring-zinc-400 dark:focus:ring-zinc-600'
              }`}
            >
              <option value={0} disabled>
                Select article...
              </option>
              {articles.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.title}
                </option>
              ))}
            </select>
            {formErrors.articleId && (
              <p className="mt-1 text-xs text-red-600 dark:text-red-400">
                {formErrors.articleId}
              </p>
            )}
          </div>

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
              placeholder="Notice content..."
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
                : editingNotice
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
        title="Delete Notice"
        message="Are you sure you want to delete this notice?"
        loading={deleting}
      />
    </div>
  );
}

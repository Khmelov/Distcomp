'use client';

import { useEffect, useState, useCallback, use } from 'react';
import { ArrowLeft, Plus, Pencil, Trash2, Clock, User } from 'lucide-react';
import Link from 'next/link';
import type {
  ArticleResponseTo,
  UserResponseTo,
  NoticeResponseTo,
  NoticeRequestTo,
  ApiError,
} from '@/app/types';
import { articlesApi, usersApi, noticesApi } from '@/app/services/api';
import { Modal } from '@/app/components/Modal';
import { ErrorAlert } from '@/app/components/ErrorAlert';
import { ConfirmDialog } from '@/app/components/ConfirmDialog';

function formatISO(date: string): string {
  const d = new Date(date);
  if (isNaN(d.getTime())) return String(date);
  return d.toISOString();
}

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

export default function ArticleDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);
  const articleId = Number(id);

  const [article, setArticle] = useState<ArticleResponseTo | null>(null);
  const [author, setAuthor] = useState<UserResponseTo | null>(null);
  const [notices, setNotices] = useState<NoticeResponseTo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [modalOpen, setModalOpen] = useState(false);
  const [editingNotice, setEditingNotice] = useState<NoticeResponseTo | null>(
    null,
  );
  const [noticeContent, setNoticeContent] = useState('');
  const [contentError, setContentError] = useState('');
  const [saving, setSaving] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState<NoticeResponseTo | null>(
    null,
  );
  const [deleting, setDeleting] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const art = await articlesApi.getById(articleId);
      setArticle(art);

      const [usr, allNotices] = await Promise.all([
        usersApi.getById(art.userId).catch(() => null),
        noticesApi.getAll(),
      ]);
      setAuthor(usr);
      setNotices(allNotices.filter((n) => n.articleId === articleId));
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to load article');
    } finally {
      setLoading(false);
    }
  }, [articleId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  function openCreate() {
    setEditingNotice(null);
    setNoticeContent('');
    setContentError('');
    setModalOpen(true);
  }

  function openEdit(notice: NoticeResponseTo) {
    setEditingNotice(notice);
    setNoticeContent(notice.content);
    setContentError('');
    setModalOpen(true);
  }

  async function handleSubmit() {
    if (noticeContent.length < 2 || noticeContent.length > 2048) {
      setContentError('Content must be 2-2048 characters');
      return;
    }
    try {
      setSaving(true);
      setError('');
      const payload: NoticeRequestTo = {
        content: noticeContent,
        articleId,
      };
      if (editingNotice) {
        await noticesApi.update(editingNotice.id, payload);
      } else {
        await noticesApi.create(payload);
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

  if (loading) {
    return (
      <div className="p-8">
        <p className="text-sm text-zinc-500 text-center py-8">Loading...</p>
      </div>
    );
  }

  if (!article) {
    return (
      <div className="p-8">
        <Link
          href="/articles"
          className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-zinc-700 dark:hover:text-zinc-300 mb-4"
        >
          <ArrowLeft size={16} />
          Back to articles
        </Link>
        <p className="text-sm text-zinc-500 text-center py-8">
          Article not found
        </p>
      </div>
    );
  }

  return (
    <div className="p-8 max-w-5xl">
      <Link
        href="/articles"
        className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-zinc-700 dark:hover:text-zinc-300 mb-6"
      >
        <ArrowLeft size={16} />
        Back to articles
      </Link>

      {error && (
        <div className="mb-4">
          <ErrorAlert message={error} onClose={() => setError('')} />
        </div>
      )}

      {/* Article card */}
      <div className="border border-zinc-200 dark:border-zinc-800 rounded-xl p-6 mb-8 bg-white dark:bg-zinc-900">
        <h1 className="text-xl font-semibold text-zinc-900 dark:text-zinc-50 mb-3">
          {article.title}
        </h1>
        <p className="text-sm text-zinc-700 dark:text-zinc-300 whitespace-pre-wrap mb-4">
          {article.content}
        </p>
        <div className="flex flex-wrap gap-4 text-xs text-zinc-500 dark:text-zinc-500">
          <span className="flex items-center gap-1.5">
            <User size={13} />
            {author
              ? `${author.firstname ?? ''} ${author.lastname ?? ''}`.trim() ||
                author.login
              : `User #${article.userId}`}
          </span>
          <span className="flex items-center gap-1.5">
            <Clock size={13} />
            Created: {formatISO(article.created)}
          </span>
          <span className="flex items-center gap-1.5">
            <Clock size={13} />
            Modified: {formatISO(article.modified)}
          </span>
        </div>
      </div>

      {/* Notices section */}
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-zinc-900 dark:text-zinc-50">
          Notices ({notices.length})
        </h2>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium rounded-lg bg-zinc-900 text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 transition-colors"
        >
          <Plus size={14} />
          Add Notice
        </button>
      </div>

      {notices.length === 0 ? (
        <p className="text-sm text-zinc-500 py-6 text-center border border-zinc-200 dark:border-zinc-800 rounded-xl">
          No notices for this article yet
        </p>
      ) : (
        <div className="space-y-3">
          {notices.map((notice) => (
            <div
              key={notice.id}
              className="border border-zinc-200 dark:border-zinc-800 rounded-xl p-4 bg-white dark:bg-zinc-900 flex items-start justify-between gap-4"
            >
              <div className="flex-1 min-w-0">
                <p className="text-sm text-zinc-800 dark:text-zinc-200 whitespace-pre-wrap">
                  {notice.content}
                </p>
                <div className="flex items-center gap-3 mt-2">
                  <span className="text-xs text-zinc-400 font-mono">
                    #{notice.id}
                  </span>
                  <span
                    className={`text-xs font-medium px-2 py-0.5 rounded-full ${stateColor(notice.state)}`}
                  >
                    {notice.state}
                  </span>
                </div>
              </div>
              <div className="flex items-center gap-1 shrink-0">
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
            </div>
          ))}
        </div>
      )}

      {/* Create / Edit Notice Modal */}
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingNotice ? 'Edit Notice' : 'New Notice'}
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
              Content
            </label>
            <textarea
              value={noticeContent}
              onChange={(e) => {
                setNoticeContent(e.target.value);
                setContentError('');
              }}
              rows={4}
              placeholder="Notice content..."
              className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 placeholder-zinc-400 focus:outline-none focus:ring-2 transition-colors resize-y ${
                contentError
                  ? 'border-red-300 focus:ring-red-400 dark:border-red-700'
                  : 'border-zinc-200 dark:border-zinc-700 focus:ring-zinc-400 dark:focus:ring-zinc-600'
              }`}
            />
            {contentError && (
              <p className="mt-1 text-xs text-red-600 dark:text-red-400">
                {contentError}
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

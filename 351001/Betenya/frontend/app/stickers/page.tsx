'use client';

import { useEffect, useState, useCallback, useMemo } from 'react';
import { Plus, Pencil, Trash2, Search } from 'lucide-react';
import type { StickerResponseTo, StickerRequestTo, ApiError } from '@/app/types';
import { stickersApi } from '@/app/services/api';
import { Modal } from '@/app/components/Modal';
import { ErrorAlert } from '@/app/components/ErrorAlert';
import { ConfirmDialog } from '@/app/components/ConfirmDialog';
import { Pagination } from '@/app/components/Pagination';

const PAGE_SIZE = 10;

export default function StickersPage() {
  const [stickers, setStickers] = useState<StickerResponseTo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingSticker, setEditingSticker] =
    useState<StickerResponseTo | null>(null);
  const [name, setName] = useState('');
  const [nameError, setNameError] = useState('');
  const [saving, setSaving] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState<StickerResponseTo | null>(
    null,
  );
  const [deleting, setDeleting] = useState(false);

  const fetchStickers = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const data = await stickersApi.getAll();
      setStickers(data);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to load stickers');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchStickers();
  }, [fetchStickers]);

  const filtered = useMemo(() => {
    if (!search.trim()) return stickers;
    const q = search.toLowerCase();
    return stickers.filter((s) => s.name.toLowerCase().includes(q));
  }, [stickers, search]);

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  function openCreate() {
    setEditingSticker(null);
    setName('');
    setNameError('');
    setModalOpen(true);
  }

  function openEdit(sticker: StickerResponseTo) {
    setEditingSticker(sticker);
    setName(sticker.name);
    setNameError('');
    setModalOpen(true);
  }

  async function handleSubmit() {
    if (name.length < 2 || name.length > 32) {
      setNameError('Name must be 2-32 characters');
      return;
    }
    try {
      setSaving(true);
      setError('');
      const payload: StickerRequestTo = { name };
      if (editingSticker) {
        await stickersApi.update(editingSticker.id, payload);
      } else {
        await stickersApi.create(payload);
      }
      setModalOpen(false);
      await fetchStickers();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to save sticker');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!deleteTarget) return;
    try {
      setDeleting(true);
      setError('');
      await stickersApi.delete(deleteTarget.id);
      setDeleteTarget(null);
      await fetchStickers();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to delete sticker');
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div className="p-8 max-w-4xl">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold text-zinc-900 dark:text-zinc-50">
          Stickers
        </h1>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-lg bg-zinc-900 text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 transition-colors"
        >
          <Plus size={16} />
          New Sticker
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
          placeholder="Search stickers by name..."
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
          {search ? 'No stickers match your search' : 'No stickers yet'}
        </p>
      ) : (
        <>
          <div className="border border-zinc-200 dark:border-zinc-800 rounded-xl overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-zinc-50 dark:bg-zinc-900 text-left text-zinc-500 dark:text-zinc-400">
                  <th className="px-4 py-3 font-medium">ID</th>
                  <th className="px-4 py-3 font-medium">Name</th>
                  <th className="px-4 py-3 font-medium w-24">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100 dark:divide-zinc-800">
                {paginated.map((sticker) => (
                  <tr
                    key={sticker.id}
                    className="hover:bg-zinc-50 dark:hover:bg-zinc-900/50 transition-colors"
                  >
                    <td className="px-4 py-3 text-zinc-600 dark:text-zinc-400 font-mono text-xs">
                      {sticker.id}
                    </td>
                    <td className="px-4 py-3 text-zinc-900 dark:text-zinc-100">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-zinc-100 text-zinc-700 dark:bg-zinc-800 dark:text-zinc-300">
                        {sticker.name}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <button
                          onClick={() => openEdit(sticker)}
                          className="p-1.5 rounded-md text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 dark:hover:bg-zinc-800 dark:hover:text-zinc-200 transition-colors"
                          title="Edit"
                        >
                          <Pencil size={15} />
                        </button>
                        <button
                          onClick={() => setDeleteTarget(sticker)}
                          className="p-1.5 rounded-md text-zinc-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950 transition-colors"
                          title="Delete"
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
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
        title={editingSticker ? 'Edit Sticker' : 'New Sticker'}
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
              Name
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => {
                setName(e.target.value);
                setNameError('');
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  e.preventDefault();
                  handleSubmit();
                }
              }}
              placeholder="Sticker name"
              className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 placeholder-zinc-400 focus:outline-none focus:ring-2 transition-colors ${
                nameError
                  ? 'border-red-300 focus:ring-red-400 dark:border-red-700'
                  : 'border-zinc-200 dark:border-zinc-700 focus:ring-zinc-400 dark:focus:ring-zinc-600'
              }`}
            />
            {nameError && (
              <p className="mt-1 text-xs text-red-600 dark:text-red-400">
                {nameError}
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
                : editingSticker
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
        title="Delete Sticker"
        message={`Are you sure you want to delete sticker "${deleteTarget?.name}"?`}
        loading={deleting}
      />
    </div>
  );
}

'use client';

import { useEffect, useState, useCallback, useMemo } from 'react';
import { Plus, Pencil, Trash2, Search } from 'lucide-react';
import type { UserResponseTo, UserRequestTo, ApiError } from '@/app/types';
import { usersApi, seedDefaultAuthor } from '@/app/services/api';
import { Modal } from '@/app/components/Modal';
import { ErrorAlert } from '@/app/components/ErrorAlert';
import { ConfirmDialog } from '@/app/components/ConfirmDialog';
import { Pagination } from '@/app/components/Pagination';

const PAGE_SIZE = 10;

const EMPTY_FORM: UserRequestTo = {
  login: '',
  password: '',
  firstname: '',
  lastname: '',
};

function validate(form: UserRequestTo): Record<string, string> {
  const errors: Record<string, string> = {};
  if (form.login.length < 2 || form.login.length > 64)
    errors.login = 'Login must be 2-64 characters';
  if (form.password.length < 8 || form.password.length > 128)
    errors.password = 'Password must be 8-128 characters';
  if (form.firstname.length < 2 || form.firstname.length > 64)
    errors.firstname = 'Firstname must be 2-64 characters';
  if (form.lastname.length < 2 || form.lastname.length > 64)
    errors.lastname = 'Lastname must be 2-64 characters';
  return errors;
}

export default function UsersPage() {
  const [users, setUsers] = useState<UserResponseTo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserResponseTo | null>(null);
  const [form, setForm] = useState<UserRequestTo>(EMPTY_FORM);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState<UserResponseTo | null>(null);
  const [deleting, setDeleting] = useState(false);

  const fetchUsers = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      await seedDefaultAuthor();
      const data = await usersApi.getAll();
      setUsers(data);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to load users');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const filtered = useMemo(() => {
    if (!search.trim()) return users;
    const q = search.toLowerCase();
    return users.filter(
      (u) =>
        u.login.toLowerCase().includes(q) ||
        (u.firstname ?? '').toLowerCase().includes(q) ||
        (u.lastname ?? '').toLowerCase().includes(q),
    );
  }, [users, search]);

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  function openCreate() {
    setEditingUser(null);
    setForm(EMPTY_FORM);
    setFormErrors({});
    setModalOpen(true);
  }

  function openEdit(user: UserResponseTo) {
    setEditingUser(user);
    setForm({
      login: user.login,
      password: '',
      firstname: user.firstname ?? '',
      lastname: user.lastname ?? '',
    });
    setFormErrors({});
    setModalOpen(true);
  }

  async function handleSubmit() {
    const validationForCreate = validate(form);
    const validationForEdit = { ...validationForCreate };
    if (editingUser && form.password === '') {
      delete validationForEdit.password;
    }
    const errors = editingUser ? validationForEdit : validationForCreate;
    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }
    try {
      setSaving(true);
      setError('');
      if (editingUser) {
        const payload: UserRequestTo = {
          ...form,
          password: form.password || 'unchanged_placeholder',
        };
        await usersApi.update(editingUser.id, payload);
      } else {
        await usersApi.create(form);
      }
      setModalOpen(false);
      await fetchUsers();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to save user');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!deleteTarget) return;
    try {
      setDeleting(true);
      setError('');
      await usersApi.delete(deleteTarget.id);
      setDeleteTarget(null);
      await fetchUsers();
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message || 'Failed to delete user');
    } finally {
      setDeleting(false);
    }
  }

  function updateField(field: keyof UserRequestTo, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
    setFormErrors((prev) => {
      const next = { ...prev };
      delete next[field];
      return next;
    });
  }

  return (
    <div className="p-8 max-w-5xl">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold text-zinc-900 dark:text-zinc-50">
          Users
        </h1>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-lg bg-zinc-900 text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 transition-colors"
        >
          <Plus size={16} />
          New User
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
          placeholder="Search users..."
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
          No users found
        </p>
      ) : (
        <>
          <div className="border border-zinc-200 dark:border-zinc-800 rounded-xl overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-zinc-50 dark:bg-zinc-900 text-left text-zinc-500 dark:text-zinc-400">
                  <th className="px-4 py-3 font-medium">ID</th>
                  <th className="px-4 py-3 font-medium">Login</th>
                  <th className="px-4 py-3 font-medium">Firstname</th>
                  <th className="px-4 py-3 font-medium">Lastname</th>
                  <th className="px-4 py-3 font-medium w-24">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100 dark:divide-zinc-800">
                {paginated.map((user) => (
                  <tr
                    key={user.id}
                    className="hover:bg-zinc-50 dark:hover:bg-zinc-900/50 transition-colors"
                  >
                    <td className="px-4 py-3 text-zinc-600 dark:text-zinc-400 font-mono text-xs">
                      {user.id}
                    </td>
                    <td className="px-4 py-3 text-zinc-900 dark:text-zinc-100">
                      {user.login}
                    </td>
                    <td className="px-4 py-3 text-zinc-700 dark:text-zinc-300">
                      {user.firstname ?? '—'}
                    </td>
                    <td className="px-4 py-3 text-zinc-700 dark:text-zinc-300">
                      {user.lastname ?? '—'}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <button
                          onClick={() => openEdit(user)}
                          className="p-1.5 rounded-md text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 dark:hover:bg-zinc-800 dark:hover:text-zinc-200 transition-colors"
                          title="Edit"
                        >
                          <Pencil size={15} />
                        </button>
                        <button
                          onClick={() => setDeleteTarget(user)}
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
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}

      {/* Create / Edit Modal */}
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingUser ? 'Edit User' : 'New User'}
      >
        <div className="space-y-4">
          <Field
            label="Login"
            value={form.login}
            error={formErrors.login}
            onChange={(v) => updateField('login', v)}
            placeholder="email@example.com"
          />
          <Field
            label={editingUser ? 'Password (leave blank to keep)' : 'Password'}
            value={form.password}
            error={formErrors.password}
            onChange={(v) => updateField('password', v)}
            type="password"
          />
          <Field
            label="Firstname"
            value={form.firstname}
            error={formErrors.firstname}
            onChange={(v) => updateField('firstname', v)}
          />
          <Field
            label="Lastname"
            value={form.lastname}
            error={formErrors.lastname}
            onChange={(v) => updateField('lastname', v)}
          />
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
              {saving ? 'Saving...' : editingUser ? 'Update' : 'Create'}
            </button>
          </div>
        </div>
      </Modal>

      {/* Delete Confirm */}
      <ConfirmDialog
        open={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete User"
        message={`Are you sure you want to delete "${deleteTarget?.login}"? This action cannot be undone.`}
        loading={deleting}
      />
    </div>
  );
}

function Field({
  label,
  value,
  error,
  onChange,
  type = 'text',
  placeholder,
}: {
  label: string;
  value: string;
  error?: string;
  onChange: (v: string) => void;
  type?: string;
  placeholder?: string;
}) {
  return (
    <div>
      <label className="block text-sm font-medium text-zinc-700 dark:text-zinc-300 mb-1">
        {label}
      </label>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-zinc-900 text-zinc-900 dark:text-zinc-50 placeholder-zinc-400 focus:outline-none focus:ring-2 transition-colors ${
          error
            ? 'border-red-300 focus:ring-red-400 dark:border-red-700'
            : 'border-zinc-200 dark:border-zinc-700 focus:ring-zinc-400 dark:focus:ring-zinc-600'
        }`}
      />
      {error && (
        <p className="mt-1 text-xs text-red-600 dark:text-red-400">{error}</p>
      )}
    </div>
  );
}

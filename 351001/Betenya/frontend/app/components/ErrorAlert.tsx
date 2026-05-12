'use client';

import { AlertTriangle, X } from 'lucide-react';

interface ErrorAlertProps {
  message: string;
  onClose: () => void;
}

export function ErrorAlert({ message, onClose }: ErrorAlertProps) {
  return (
    <div className="flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3 dark:border-red-900 dark:bg-red-950">
      <AlertTriangle size={18} className="mt-0.5 text-red-600 dark:text-red-400 shrink-0" />
      <p className="flex-1 text-sm text-red-700 dark:text-red-300">{message}</p>
      <button
        onClick={onClose}
        className="p-0.5 text-red-400 hover:text-red-600 dark:hover:text-red-200 transition-colors"
      >
        <X size={16} />
      </button>
    </div>
  );
}

import React from "react";
import { AlertCircle, RefreshCw } from "lucide-react";

export const LoadingState: React.FC<{ message?: string }> = ({ message = "Loading..." }) => (
  <div className="flex items-center justify-center py-16">
    <div className="text-center">
      <RefreshCw className="mx-auto mb-3 animate-spin text-slate-400" size={28} />
      <p className="text-sm text-slate-500">{message}</p>
    </div>
  </div>
);

export const ErrorState: React.FC<{ message: string; onRetry?: () => void }> = ({ message, onRetry }) => (
  <div className="rounded-panel border border-red-200 bg-red-50 p-5 text-center">
    <AlertCircle className="mx-auto mb-2 text-red-500" size={24} />
    <p className="text-sm text-red-700">{message}</p>
    {onRetry && (
      <button
        className="mt-3 rounded-lg bg-red-600 px-3 py-2 text-xs font-medium text-white hover:bg-red-700"
        onClick={onRetry}
      >
        Retry
      </button>
    )}
  </div>
);

export const EmptyState: React.FC<{ title: string; description?: string }> = ({ title, description }) => (
  <div className="rounded-panel border border-slate-200 bg-white px-6 py-10 text-center">
    <p className="text-base font-semibold text-slate-700">{title}</p>
    {description && <p className="mt-1 text-sm text-slate-500">{description}</p>}
  </div>
);

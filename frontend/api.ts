import { auth, config } from "./config";

type QueueItem = {
  resolve: () => void;
  reject: (reason?: unknown) => void;
};

let isRefreshing = false;
let failedQueue: QueueItem[] = [];

const processQueue = (error?: unknown) => {
  failedQueue.forEach((item) => {
    if (error) {
      item.reject(error);
      return;
    }
    item.resolve();
  });
  failedQueue = [];
};

const redirectToLogin = () => {
  auth.removeToken();
  window.dispatchEvent(new CustomEvent("auth:expired"));
};

export const apiFetch = async (url: string, options: RequestInit = {}): Promise<Response> => {
  const headers = new Headers(options.headers);
  const isFormData = options.body instanceof FormData;
  if (!headers.has("Content-Type") && !isFormData) {
    headers.set("Content-Type", "application/json");
  }

  if (auth.isAuthenticated()) {
    headers.set("Authorization", auth.getAuthHeader());
  }

  const requestOptions: RequestInit = {
    ...options,
    headers,
  };

  let response = await fetch(url, requestOptions);

  if (response.status === 401) {
    if (isRefreshing) {
      await new Promise<void>((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      });
      headers.set("Authorization", auth.getAuthHeader());
      return fetch(url, { ...requestOptions, headers });
    }

    isRefreshing = true;
    try {
      const refreshed = await auth.refreshAccessToken();
      if (!refreshed) {
        processQueue(new Error("Token refresh failed"));
        redirectToLogin();
        throw new Error("Authentication expired. Please login again.");
      }
      processQueue();
      headers.set("Authorization", auth.getAuthHeader());
      response = await fetch(url, { ...requestOptions, headers });
    } catch (error) {
      processQueue(error);
      redirectToLogin();
      throw error;
    } finally {
      isRefreshing = false;
    }
  }

  if (response.status === 401) {
    redirectToLogin();
  }

  return response;
};

const toError = async (response: Response) => {
  const errorText = await response.text();
  throw new Error(`HTTP ${response.status}: ${errorText || response.statusText}`);
};

export const api = {
  get: async <T>(endpoint: string): Promise<T> => {
    const response = await apiFetch(`${config.apiUrl}${endpoint}`, { method: "GET" });
    if (!response.ok) {
      return toError(response);
    }
    return response.json() as Promise<T>;
  },

  post: async <T>(endpoint: string, data: unknown): Promise<T> => {
    const response = await apiFetch(`${config.apiUrl}${endpoint}`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      return toError(response);
    }
    return response.json() as Promise<T>;
  },

  put: async <T>(endpoint: string, data: unknown): Promise<T> => {
    const response = await apiFetch(`${config.apiUrl}${endpoint}`, {
      method: "PUT",
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      return toError(response);
    }
    return response.json() as Promise<T>;
  },

  delete: async (endpoint: string): Promise<void> => {
    const response = await apiFetch(`${config.apiUrl}${endpoint}`, { method: "DELETE" });
    if (!response.ok) {
      return toError(response);
    }
  },
};

export default api;

import { clearSession, readSession } from "./session";

// One thin fetch wrapper: relative /api urls ( Vite proxies them to the
// backend ), the Bearer token when there is one, and typed errors.

export type FieldErrors = Record<string, string>;

export class ApiError extends Error {
  status: number;
  code?: string;
  errors?: FieldErrors;

  constructor(status: number, message: string, code?: string, errors?: FieldErrors) {
    super(message);
    this.status = status;
    this.code = code;
    this.errors = errors;
  }
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  const session = readSession();
  if (session) headers.set("Authorization", `Bearer ${session.token}`);

  const response = await fetch(path, { ...init, headers });

  if (response.status === 401 && session) {
    clearSession();
    window.location.assign("/login");
  }

  if (!response.ok) {
    const body = (await response.json().catch(() => ({}))) as {
      message?: string;
      code?: string;
      errors?: FieldErrors;
    };
    throw new ApiError(response.status, body.message ?? response.statusText, body.code, body.errors);
  }

  if (response.status === 204) return undefined as T;
  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

function json(body: unknown, method = "POST"): RequestInit {
  return { method, headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) };
}

export type Recipe = {
  id: number;
  title: string;
  cuisine: string;
  servings: number;
  vegetarian: boolean;
  cookId: number;
  cookName: string;
};

export type Cook = {
  id: number;
  name: string;
  email: string;
  level: "HOME" | "PRO";
  recipeCount: number;
  recipes: string[];
};

export type ShoppingItem = { id: number; item: string; quantity: string; forRecipe: string };

export type LoginResponse = { token: string; name: string; email: string };

export const api = {
  login: (email: string, password: string) =>
    request<LoginResponse>("/api/auth/login", json({ email, password })),

  recipes: (q: string) => request<Recipe[]>(`/api/recipes${q ? `?q=${encodeURIComponent(q)}` : ""}`),
  addRecipe: (recipe: { title: string; cuisine: string; cookId: number | null; servings: number; vegetarian: boolean }) =>
    request<Recipe>("/api/recipes", json(recipe)),
  deleteRecipe: (id: number) => request<void>(`/api/recipes/${id}`, { method: "DELETE" }),

  cooks: () => request<Cook[]>("/api/cooks"),
  cook: (id: string) => request<Cook>(`/api/cooks/${id}`),
  addCook: (cook: { name: string; email: string; level: string }) => request<Cook>("/api/cooks", json(cook)),
  removeCook: (id: number) => request<void>(`/api/cooks/${id}`, { method: "DELETE" }),

  shoppingList: () => request<ShoppingItem[]>("/api/shopping-list"),
};

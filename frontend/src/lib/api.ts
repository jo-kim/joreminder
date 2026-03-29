import { ReminderList, Reminder, Priority } from "./types";

const BASE = "/api";

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${url}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    throw new Error(`API error: ${res.status}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

// Lists
export const fetchLists = () => request<ReminderList[]>("/lists");

export const createList = (body: { name: string; color: string }) =>
  request<ReminderList>("/lists", {
    method: "POST",
    body: JSON.stringify(body),
  });

export const updateList = (id: number, body: { name: string; color: string }) =>
  request<ReminderList>(`/lists/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });

export const deleteList = (id: number) =>
  request<void>(`/lists/${id}`, { method: "DELETE" });

// Reminders
export const fetchReminders = (listId: number) =>
  request<Reminder[]>(`/lists/${listId}/reminders`);

export interface ReminderBody {
  title: string;
  listId: number;
  memo?: string | null;
  dueDate?: string | null;
  dueTime?: string | null;
  priority?: Priority | null;
}

export const createReminder = (body: ReminderBody) =>
  request<Reminder>("/reminders", {
    method: "POST",
    body: JSON.stringify(body),
  });

export const updateReminder = (id: number, body: ReminderBody) =>
  request<Reminder>(`/reminders/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });

export const toggleReminder = (id: number) =>
  request<Reminder>(`/reminders/${id}/toggle`, { method: "PATCH" });

export const deleteReminder = (id: number) =>
  request<void>(`/reminders/${id}`, { method: "DELETE" });

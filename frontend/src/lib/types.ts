export interface ReminderList {
  id: number;
  name: string;
  color: string;
  isDefault: boolean;
  reminderCount: number;
  createdAt: string;
  updatedAt: string;
}

export type Priority = "NONE" | "LOW" | "MEDIUM" | "HIGH";

export interface Reminder {
  id: number;
  title: string;
  memo: string | null;
  completed: boolean;
  completedAt: string | null;
  dueDate: string | null;
  dueTime: string | null;
  priority: Priority;
  displayOrder: number;
  listId: number;
  createdAt: string;
  updatedAt: string;
}

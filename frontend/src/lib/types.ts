export interface ReminderList {
  id: number;
  name: string;
  color: string;
  isDefault: boolean;
  reminderCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Reminder {
  id: number;
  title: string;
  completed: boolean;
  listId: number;
  createdAt: string;
  updatedAt: string;
}

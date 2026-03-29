"use client";

import { Reminder } from "@/lib/types";
import styles from "@/app/layout.module.css";

interface ReminderRowProps {
  reminder: Reminder;
  listColor: string;
  onToggle: (id: number) => void;
}

export default function ReminderRow({
  reminder,
  listColor,
  onToggle,
}: ReminderRowProps) {
  return (
    <div className={styles.reminderRow}>
      <button
        className={`${styles.checkbox} ${
          reminder.completed ? styles.checkboxCompleted : ""
        }`}
        style={
          reminder.completed
            ? { backgroundColor: listColor }
            : { borderColor: listColor }
        }
        onClick={() => onToggle(reminder.id)}
      >
        {reminder.completed && <span className={styles.checkmark}>✓</span>}
      </button>
      <span
        className={`${styles.reminderTitle} ${
          reminder.completed ? styles.reminderTitleCompleted : ""
        }`}
      >
        {reminder.title}
      </span>
    </div>
  );
}

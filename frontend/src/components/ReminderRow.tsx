"use client";

import { useState, useRef, useEffect } from "react";
import { Reminder } from "@/lib/types";
import styles from "@/app/layout.module.css";

interface ReminderRowProps {
  reminder: Reminder;
  listColor: string;
  onToggle: (id: number) => void;
  onUpdate: (id: number, title: string) => void;
  onDelete: (id: number) => void;
}

export default function ReminderRow({
  reminder,
  listColor,
  onToggle,
  onUpdate,
  onDelete,
}: ReminderRowProps) {
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(reminder.title);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (editing && inputRef.current) {
      inputRef.current.focus();
    }
  }, [editing]);

  const handleSave = () => {
    setEditing(false);
    const trimmed = title.trim();
    if (trimmed && trimmed !== reminder.title) {
      onUpdate(reminder.id, trimmed);
    } else {
      setTitle(reminder.title);
    }
  };

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
      <div className={styles.reminderContent}>
        {editing ? (
          <input
            ref={inputRef}
            className={styles.reminderTitleInput}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            onBlur={handleSave}
            onKeyDown={(e) => {
              if (e.key === "Enter") handleSave();
              if (e.key === "Escape") {
                setTitle(reminder.title);
                setEditing(false);
              }
            }}
          />
        ) : (
          <span
            className={`${styles.reminderTitle} ${
              reminder.completed ? styles.reminderTitleCompleted : ""
            }`}
            onClick={() => !reminder.completed && setEditing(true)}
          >
            {reminder.title}
          </span>
        )}
      </div>
      <button
        className={styles.deleteButton}
        onClick={() => onDelete(reminder.id)}
        title="삭제"
      >
        ✕
      </button>
    </div>
  );
}

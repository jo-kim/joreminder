"use client";

import { useState, useRef, useEffect } from "react";
import { Reminder, ReminderList, Priority } from "@/lib/types";
import { getColor } from "@/lib/colors";
import ReminderRow from "./ReminderRow";
import ConfirmDialog from "./ConfirmDialog";
import styles from "@/app/layout.module.css";

interface MainContentProps {
  list: ReminderList | null;
  reminders: Reminder[];
  onToggle: (id: number) => void;
  onCreate: (title: string) => void;
  onUpdate: (
    id: number,
    fields: {
      title: string;
      memo?: string | null;
      dueDate?: string | null;
      dueTime?: string | null;
      priority?: Priority | null;
    }
  ) => void;
  onDelete: (id: number) => void;
}

export default function MainContent({
  list,
  reminders,
  onToggle,
  onCreate,
  onUpdate,
  onDelete,
}: MainContentProps) {
  const [adding, setAdding] = useState(false);
  const [newTitle, setNewTitle] = useState("");
  const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (adding && inputRef.current) {
      inputRef.current.focus();
    }
  }, [adding]);

  if (!list) {
    return (
      <main className={styles.main}>
        <div className={styles.emptyState}>목록을 선택하세요</div>
      </main>
    );
  }

  const color = getColor(list.color);

  const handleCreate = () => {
    const trimmed = newTitle.trim();
    if (trimmed) {
      onCreate(trimmed);
      setNewTitle("");
    } else {
      setAdding(false);
      setNewTitle("");
    }
  };

  const handleDeleteConfirm = () => {
    if (deleteTarget !== null) {
      onDelete(deleteTarget);
      setDeleteTarget(null);
    }
  };

  return (
    <main className={styles.main}>
      <div className={styles.mainHeader}>
        <h1 className={styles.mainTitle} style={{ color }}>
          {list.name}
        </h1>
      </div>
      <div className={styles.reminderList}>
        {reminders.map((r) => (
          <ReminderRow
            key={r.id}
            reminder={r}
            listColor={color}
            onToggle={onToggle}
            onUpdate={onUpdate}
            onDelete={(id) => setDeleteTarget(id)}
          />
        ))}

        {adding ? (
          <div className={styles.newReminderRow}>
            <div
              className={styles.checkbox}
              style={{ borderColor: color }}
            />
            <input
              ref={inputRef}
              className={styles.reminderTitleInput}
              placeholder="새로운 미리 알림"
              value={newTitle}
              onChange={(e) => setNewTitle(e.target.value)}
              onBlur={handleCreate}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  e.preventDefault();
                  handleCreate();
                }
                if (e.key === "Escape") {
                  setAdding(false);
                  setNewTitle("");
                }
              }}
            />
          </div>
        ) : (
          <button
            className={styles.addButton}
            onClick={() => setAdding(true)}
          >
            <span
              className={styles.addButtonIcon}
              style={{ backgroundColor: color }}
            >
              +
            </span>
            <span className={styles.addButtonText} style={{ color }}>
              새로운 미리 알림
            </span>
          </button>
        )}

        {reminders.length === 0 && !adding && (
          <div className={styles.emptyState}>리마인더가 없습니다</div>
        )}
      </div>

      {deleteTarget !== null && (
        <ConfirmDialog
          title="리마인더 삭제"
          message="이 리마인더를 삭제하시겠습니까?"
          onConfirm={handleDeleteConfirm}
          onCancel={() => setDeleteTarget(null)}
        />
      )}
    </main>
  );
}

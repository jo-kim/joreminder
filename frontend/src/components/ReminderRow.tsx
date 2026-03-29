"use client";

import { useState, useRef, useEffect } from "react";
import { Reminder, Priority } from "@/lib/types";
import styles from "@/app/layout.module.css";

const PRIORITY_LABELS: Record<Priority, string> = {
  NONE: "없음",
  LOW: "!",
  MEDIUM: "!!",
  HIGH: "!!!",
};

interface ReminderRowProps {
  reminder: Reminder;
  listColor: string;
  onToggle: (id: number) => void;
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

export default function ReminderRow({
  reminder,
  listColor,
  onToggle,
  onUpdate,
  onDelete,
}: ReminderRowProps) {
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(reminder.title);
  const [memo, setMemo] = useState(reminder.memo ?? "");
  const [dueDate, setDueDate] = useState(reminder.dueDate ?? "");
  const [dueTime, setDueTime] = useState(reminder.dueTime ?? "");
  const [priority, setPriority] = useState<Priority>(reminder.priority);
  const titleRef = useRef<HTMLInputElement>(null);
  const rowRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (editing && titleRef.current) {
      titleRef.current.focus();
    }
  }, [editing]);

  useEffect(() => {
    setTitle(reminder.title);
    setMemo(reminder.memo ?? "");
    setDueDate(reminder.dueDate ?? "");
    setDueTime(reminder.dueTime ?? "");
    setPriority(reminder.priority);
  }, [reminder]);

  useEffect(() => {
    if (!editing) return;
    const handleClickOutside = (e: MouseEvent) => {
      if (rowRef.current && !rowRef.current.contains(e.target as Node)) {
        handleSave();
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  });

  const handleSave = () => {
    setEditing(false);
    const trimmedTitle = title.trim();
    if (!trimmedTitle) {
      setTitle(reminder.title);
      return;
    }
    const trimmedMemo = memo.trim() || null;
    const hasChanged =
      trimmedTitle !== reminder.title ||
      trimmedMemo !== reminder.memo ||
      (dueDate || null) !== reminder.dueDate ||
      (dueTime || null) !== reminder.dueTime ||
      priority !== reminder.priority;

    if (hasChanged) {
      onUpdate(reminder.id, {
        title: trimmedTitle,
        memo: trimmedMemo,
        dueDate: dueDate || null,
        dueTime: dueTime || null,
        priority,
      });
    }
  };

  const formatDueDate = (d: string) => {
    const date = new Date(d + "T00:00:00");
    return date.toLocaleDateString("ko-KR", {
      month: "long",
      day: "numeric",
    });
  };

  return (
    <div ref={rowRef} className={styles.reminderRow}>
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
          <div className={styles.reminderExpanded}>
            <input
              ref={titleRef}
              className={styles.reminderTitleInput}
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleSave();
                if (e.key === "Escape") {
                  setTitle(reminder.title);
                  setMemo(reminder.memo ?? "");
                  setDueDate(reminder.dueDate ?? "");
                  setDueTime(reminder.dueTime ?? "");
                  setPriority(reminder.priority);
                  setEditing(false);
                }
              }}
              placeholder="제목"
            />
            <textarea
              className={styles.memoInput}
              value={memo}
              onChange={(e) => setMemo(e.target.value)}
              placeholder="메모"
              rows={2}
            />
            <div className={styles.detailRow}>
              <label className={styles.detailLabel}>마감일</label>
              <input
                type="date"
                className={styles.detailInput}
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
              />
            </div>
            <div className={styles.detailRow}>
              <label className={styles.detailLabel}>마감시간</label>
              <input
                type="time"
                className={styles.detailInput}
                value={dueTime}
                onChange={(e) => setDueTime(e.target.value)}
              />
            </div>
            <div className={styles.detailRow}>
              <label className={styles.detailLabel}>우선순위</label>
              <div className={styles.priorityGroup}>
                {(["NONE", "LOW", "MEDIUM", "HIGH"] as Priority[]).map((p) => (
                  <button
                    key={p}
                    className={`${styles.priorityButton} ${
                      priority === p ? styles.priorityButtonActive : ""
                    }`}
                    onClick={() => setPriority(p)}
                  >
                    {PRIORITY_LABELS[p]}
                  </button>
                ))}
              </div>
            </div>
          </div>
        ) : (
          <div onClick={() => !reminder.completed && setEditing(true)}>
            <span
              className={`${styles.reminderTitle} ${
                reminder.completed ? styles.reminderTitleCompleted : ""
              }`}
            >
              {reminder.priority !== "NONE" && (
                <span className={styles.priorityIndicator} style={{ color: listColor }}>
                  {PRIORITY_LABELS[reminder.priority]}{" "}
                </span>
              )}
              {reminder.title}
            </span>
            {(reminder.dueDate || reminder.memo) && (
              <div className={styles.reminderSubtext}>
                {reminder.dueDate && (
                  <span className={styles.dueDatePreview}>
                    {formatDueDate(reminder.dueDate)}
                    {reminder.dueTime && ` ${reminder.dueTime}`}
                  </span>
                )}
                {reminder.memo && (
                  <span className={styles.memoPreview}>{reminder.memo}</span>
                )}
              </div>
            )}
          </div>
        )}
      </div>
      {!editing && (
        <button
          className={styles.deleteButton}
          onClick={() => onDelete(reminder.id)}
          title="삭제"
        >
          ✕
        </button>
      )}
    </div>
  );
}

"use client";

import { useState, useRef, useEffect, useCallback } from "react";
import { ReminderList } from "@/lib/types";
import { COLOR_MAP, getColor } from "@/lib/colors";
import styles from "@/app/layout.module.css";

const COLOR_KEYS = Object.keys(COLOR_MAP);

interface ListModalProps {
  list: ReminderList | null;
  onSave: (name: string, color: string) => void;
  onCancel: () => void;
}

export default function ListModal({ list, onSave, onCancel }: ListModalProps) {
  const [name, setName] = useState(list?.name ?? "");
  const [color, setColor] = useState(list?.color ?? "BLUE");
  const inputRef = useRef<HTMLInputElement>(null);
  const submittedRef = useRef(false);

  const handleEscape = useCallback(
    (e: KeyboardEvent) => {
      if (e.key === "Escape") onCancel();
    },
    [onCancel]
  );

  useEffect(() => {
    inputRef.current?.focus();
    document.addEventListener("keydown", handleEscape);
    return () => document.removeEventListener("keydown", handleEscape);
  }, [handleEscape]);

  const handleSubmit = () => {
    if (submittedRef.current) return;
    const trimmed = name.trim();
    if (trimmed) {
      submittedRef.current = true;
      onSave(trimmed, color);
    }
  };

  return (
    <div className={styles.dialogOverlay} onClick={onCancel}>
      <div className={styles.dialog} onClick={(e) => e.stopPropagation()}>
        <div className={styles.dialogTitle}>
          {list ? "목록 수정" : "새로운 목록"}
        </div>

        <div className={styles.modalField}>
          <input
            ref={inputRef}
            className={styles.modalInput}
            placeholder="목록 이름"
            value={name}
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") handleSubmit();
              if (e.key === "Escape") onCancel();
            }}
          />
        </div>

        <div className={styles.colorPalette}>
          {COLOR_KEYS.map((key) => (
            <button
              key={key}
              className={`${styles.colorSwatch} ${
                color === key ? styles.colorSwatchSelected : ""
              }`}
              style={{ backgroundColor: getColor(key) }}
              onClick={() => setColor(key)}
              title={key}
            />
          ))}
        </div>

        <div className={styles.dialogActions}>
          <button className={styles.dialogCancel} onClick={onCancel}>
            취소
          </button>
          <button
            className={styles.dialogSave}
            onClick={handleSubmit}
            disabled={!name.trim()}
          >
            {list ? "수정" : "생성"}
          </button>
        </div>
      </div>
    </div>
  );
}

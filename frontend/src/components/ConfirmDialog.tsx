"use client";

import { useEffect } from "react";
import styles from "@/app/layout.module.css";

interface ConfirmDialogProps {
  title: string;
  message: string;
  confirmLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export default function ConfirmDialog({
  title,
  message,
  confirmLabel = "확인",
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onCancel();
    };
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [onCancel]);

  return (
    <div className={styles.dialogOverlay} onClick={onCancel}>
      <div
        className={styles.dialog}
        role="alertdialog"
        aria-labelledby="dialog-title"
        onClick={(e) => e.stopPropagation()}
      >
        <div id="dialog-title" className={styles.dialogTitle}>{title}</div>
        <div className={styles.dialogMessage}>{message}</div>
        <div className={styles.dialogActions}>
          <button className={styles.dialogCancel} onClick={onCancel}>
            취소
          </button>
          <button className={styles.dialogConfirm} onClick={onConfirm}>
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

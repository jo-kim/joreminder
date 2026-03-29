"use client";

import styles from "@/app/layout.module.css";

interface ConfirmDialogProps {
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export default function ConfirmDialog({
  title,
  message,
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  return (
    <div className={styles.dialogOverlay} onClick={onCancel}>
      <div className={styles.dialog} onClick={(e) => e.stopPropagation()}>
        <div className={styles.dialogTitle}>{title}</div>
        <div className={styles.dialogMessage}>{message}</div>
        <div className={styles.dialogActions}>
          <button className={styles.dialogCancel} onClick={onCancel}>
            취소
          </button>
          <button className={styles.dialogConfirm} onClick={onConfirm}>
            삭제
          </button>
        </div>
      </div>
    </div>
  );
}

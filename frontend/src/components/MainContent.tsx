"use client";

import { Reminder, ReminderList } from "@/lib/types";
import { getColor } from "@/lib/colors";
import ReminderRow from "./ReminderRow";
import styles from "@/app/layout.module.css";

interface MainContentProps {
  list: ReminderList | null;
  reminders: Reminder[];
  onToggle: (id: number) => void;
}

export default function MainContent({
  list,
  reminders,
  onToggle,
}: MainContentProps) {
  if (!list) {
    return (
      <main className={styles.main}>
        <div className={styles.emptyState}>목록을 선택하세요</div>
      </main>
    );
  }

  const color = getColor(list.color);

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
          />
        ))}
        {reminders.length === 0 && (
          <div className={styles.emptyState}>리마인더가 없습니다</div>
        )}
      </div>
    </main>
  );
}

"use client";

import { ReminderList } from "@/lib/types";
import { getColor } from "@/lib/colors";
import styles from "@/app/layout.module.css";

interface SidebarProps {
  lists: ReminderList[];
  selectedId: number | null;
  onSelect: (id: number) => void;
}

export default function Sidebar({ lists, selectedId, onSelect }: SidebarProps) {
  return (
    <aside className={styles.sidebar}>
      <div className={styles.sidebarSection}>
        <div className={styles.sidebarTitle}>나의 목록</div>
        {lists.map((list) => (
          <div
            key={list.id}
            className={`${styles.listItem} ${
              selectedId === list.id ? styles.listItemSelected : ""
            }`}
            onClick={() => onSelect(list.id)}
          >
            <div
              className={styles.listBullet}
              style={{ backgroundColor: getColor(list.color) }}
            />
            <span className={styles.listName}>{list.name}</span>
            <span className={styles.listCount}>
              {list.reminderCount > 0 ? list.reminderCount : ""}
            </span>
          </div>
        ))}
      </div>
    </aside>
  );
}

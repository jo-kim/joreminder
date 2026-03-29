"use client";

import { useState } from "react";
import { ReminderList } from "@/lib/types";
import { getColor } from "@/lib/colors";
import ContextMenu from "./ContextMenu";
import styles from "@/app/layout.module.css";

interface SidebarProps {
  lists: ReminderList[];
  selectedId: number | null;
  onSelect: (id: number) => void;
  onAddList: () => void;
  onEditList: (list: ReminderList) => void;
  onDeleteList: (list: ReminderList) => void;
}

interface ContextState {
  x: number;
  y: number;
  list: ReminderList;
}

export default function Sidebar({
  lists,
  selectedId,
  onSelect,
  onAddList,
  onEditList,
  onDeleteList,
}: SidebarProps) {
  const [context, setContext] = useState<ContextState | null>(null);

  const handleContextMenu = (e: React.MouseEvent, list: ReminderList) => {
    e.preventDefault();
    setContext({ x: e.clientX, y: e.clientY, list });
  };

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
            onContextMenu={(e) => handleContextMenu(e, list)}
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

      <div className={styles.sidebarFooter}>
        <button className={styles.addListButton} onClick={onAddList}>
          <span className={styles.addListIcon}>+</span>
          목록 추가
        </button>
      </div>

      {context && (
        <ContextMenu
          x={context.x}
          y={context.y}
          items={[
            {
              label: "수정",
              onClick: () => onEditList(context.list),
            },
            {
              label: "삭제",
              onClick: () => onDeleteList(context.list),
              disabled: context.list.isDefault,
              danger: true,
            },
          ]}
          onClose={() => setContext(null)}
        />
      )}
    </aside>
  );
}

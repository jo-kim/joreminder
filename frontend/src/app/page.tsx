"use client";

import { useEffect, useState, useCallback, useRef } from "react";
import { ReminderList, Reminder, Priority } from "@/lib/types";
import {
  fetchLists,
  fetchReminders,
  createReminder,
  updateReminder,
  toggleReminder,
  deleteReminder,
  createList,
  updateList,
  deleteList,
} from "@/lib/api";
import Sidebar from "@/components/Sidebar";
import MainContent from "@/components/MainContent";
import ListModal from "@/components/ListModal";
import ConfirmDialog from "@/components/ConfirmDialog";
import styles from "./layout.module.css";

type ModalMode =
  | { type: "create" }
  | { type: "edit"; list: ReminderList }
  | null;

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [deleteTarget, setDeleteTarget] = useState<ReminderList | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const initializedRef = useRef(false);

  const loadLists = useCallback(async () => {
    try {
      const data = await fetchLists();
      setLists(data);
      return data;
    } catch {
      setError("목록을 불러오는데 실패했습니다.");
      return [];
    }
  }, []);

  const loadReminders = useCallback(async () => {
    if (selectedId === null) {
      setReminders([]);
      return;
    }
    try {
      const data = await fetchReminders(selectedId);
      setReminders(data);
    } catch {
      setError("리마인더를 불러오는데 실패했습니다.");
    }
  }, [selectedId]);

  // Initial load — select first list only once
  useEffect(() => {
    if (initializedRef.current) return;
    initializedRef.current = true;
    (async () => {
      const data = await loadLists();
      if (data.length > 0) {
        setSelectedId(data[0].id);
      }
      setLoading(false);
    })();
  }, [loadLists]);

  useEffect(() => {
    loadReminders();
  }, [loadReminders]);

  const withErrorHandling = async (fn: () => Promise<void>) => {
    try {
      setError(null);
      await fn();
    } catch {
      setError("작업 중 오류가 발생했습니다.");
    }
  };

  const refresh = async () => {
    await loadReminders();
    await loadLists();
  };

  // Reminder handlers
  const handleToggle = (id: number) =>
    withErrorHandling(async () => {
      await toggleReminder(id);
      await refresh();
    });

  const handleCreate = (title: string) =>
    withErrorHandling(async () => {
      if (selectedId === null) return;
      await createReminder({ title, listId: selectedId });
      await refresh();
    });

  const handleUpdate = (
    id: number,
    fields: {
      title: string;
      memo?: string | null;
      dueDate?: string | null;
      dueTime?: string | null;
      priority?: Priority | null;
    }
  ) =>
    withErrorHandling(async () => {
      if (selectedId === null) return;
      await updateReminder(id, { ...fields, listId: selectedId });
      await refresh();
    });

  const handleDelete = (id: number) =>
    withErrorHandling(async () => {
      await deleteReminder(id);
      await refresh();
    });

  // List handlers
  const handleListSave = (name: string, color: string) =>
    withErrorHandling(async () => {
      if (modalMode?.type === "edit") {
        await updateList(modalMode.list.id, { name, color });
      } else {
        const created = await createList({ name, color });
        setSelectedId(created.id);
      }
      setModalMode(null);
      await loadLists();
    });

  const handleListDelete = () =>
    withErrorHandling(async () => {
      if (!deleteTarget) return;
      await deleteList(deleteTarget.id);
      setDeleteTarget(null);
      if (selectedId === deleteTarget.id) {
        const remaining = lists.filter((l) => l.id !== deleteTarget.id);
        setSelectedId(remaining.length > 0 ? remaining[0].id : null);
      }
      await loadLists();
    });

  const selectedList = lists.find((l) => l.id === selectedId) ?? null;

  return (
    <div className={styles.container}>
      <Sidebar
        lists={lists}
        selectedId={selectedId}
        onSelect={setSelectedId}
        onAddList={() => setModalMode({ type: "create" })}
        onEditList={(list) => setModalMode({ type: "edit", list })}
        onDeleteList={(list) => setDeleteTarget(list)}
      />
      <MainContent
        list={selectedList}
        reminders={reminders}
        onToggle={handleToggle}
        onCreate={handleCreate}
        onUpdate={handleUpdate}
        onDelete={handleDelete}
        loading={loading}
        error={error}
        onDismissError={() => setError(null)}
      />

      {modalMode && (
        <ListModal
          list={modalMode.type === "edit" ? modalMode.list : null}
          onSave={handleListSave}
          onCancel={() => setModalMode(null)}
        />
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="목록 삭제"
          message={`"${deleteTarget.name}" 목록과 포함된 모든 리마인더가 삭제됩니다.`}
          confirmLabel="삭제"
          onConfirm={handleListDelete}
          onCancel={() => setDeleteTarget(null)}
        />
      )}
    </div>
  );
}

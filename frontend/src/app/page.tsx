"use client";

import { useEffect, useState, useCallback } from "react";
import { ReminderList, Reminder } from "@/lib/types";
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

  const loadLists = useCallback(async () => {
    const data = await fetchLists();
    setLists(data);
    if (data.length > 0 && selectedId === null) {
      setSelectedId(data[0].id);
    }
  }, [selectedId]);

  const loadReminders = useCallback(async () => {
    if (selectedId === null) return;
    const data = await fetchReminders(selectedId);
    setReminders(data);
  }, [selectedId]);

  useEffect(() => {
    loadLists();
  }, [loadLists]);

  useEffect(() => {
    loadReminders();
  }, [loadReminders]);

  const refresh = async () => {
    await loadReminders();
    await loadLists();
  };

  // Reminder handlers
  const handleToggle = async (id: number) => {
    await toggleReminder(id);
    await refresh();
  };

  const handleCreate = async (title: string) => {
    if (selectedId === null) return;
    await createReminder({ title, listId: selectedId });
    await refresh();
  };

  const handleUpdate = async (id: number, title: string) => {
    if (selectedId === null) return;
    await updateReminder(id, { title, listId: selectedId });
    await refresh();
  };

  const handleDelete = async (id: number) => {
    await deleteReminder(id);
    await refresh();
  };

  // List handlers
  const handleListSave = async (name: string, color: string) => {
    if (modalMode?.type === "edit") {
      await updateList(modalMode.list.id, { name, color });
    } else {
      const created = await createList({ name, color });
      setSelectedId(created.id);
    }
    setModalMode(null);
    await loadLists();
  };

  const handleListDelete = async () => {
    if (!deleteTarget) return;
    await deleteList(deleteTarget.id);
    setDeleteTarget(null);
    if (selectedId === deleteTarget.id) {
      const remaining = lists.filter((l) => l.id !== deleteTarget.id);
      setSelectedId(remaining.length > 0 ? remaining[0].id : null);
    }
    await loadLists();
  };

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
          onConfirm={handleListDelete}
          onCancel={() => setDeleteTarget(null)}
        />
      )}
    </div>
  );
}

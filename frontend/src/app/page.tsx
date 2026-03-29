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
} from "@/lib/api";
import Sidebar from "@/components/Sidebar";
import MainContent from "@/components/MainContent";
import styles from "./layout.module.css";

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [reminders, setReminders] = useState<Reminder[]>([]);

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

  const selectedList = lists.find((l) => l.id === selectedId) ?? null;

  return (
    <div className={styles.container}>
      <Sidebar lists={lists} selectedId={selectedId} onSelect={setSelectedId} />
      <MainContent
        list={selectedList}
        reminders={reminders}
        onToggle={handleToggle}
        onCreate={handleCreate}
        onUpdate={handleUpdate}
        onDelete={handleDelete}
      />
    </div>
  );
}

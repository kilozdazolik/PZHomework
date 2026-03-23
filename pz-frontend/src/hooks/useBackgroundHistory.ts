import { useCallback, useMemo, useState } from 'react';

const MAX_BACKGROUND_HISTORY = 12;

const normalizeHistory = (entries: string[]): string[] => entries
  .map((entry) => entry.trim())
  .filter((entry) => entry.length > 0)
  .filter((entry, index, list) => list.indexOf(entry) === index)
  .slice(0, MAX_BACKGROUND_HISTORY);

const readHistoryFromStorage = (storageKey: string): string[] => {
  const rawHistory = localStorage.getItem(storageKey);
  const parsedHistory = rawHistory ? (JSON.parse(rawHistory) as unknown) : [];

  if (!Array.isArray(parsedHistory)) {
    return [];
  }

  return parsedHistory.filter((entry): entry is string => typeof entry === 'string' && entry.trim().length > 0);
};

export function useBackgroundHistory(userId: string, currentBackgroundUrl?: string) {
  const [revision, setRevision] = useState(0);
  const storageKey = useMemo(() => `desktop-background-history:${userId}`, [userId]);

  const backgroundHistory = useMemo(() => {
    if (!userId) {
      return [];
    }

    const fromStorage = readHistoryFromStorage(storageKey);
    const fromCurrent = currentBackgroundUrl ? [currentBackgroundUrl] : [];

    return normalizeHistory([...fromCurrent, ...fromStorage]);
  }, [storageKey, userId, currentBackgroundUrl, revision]);

  const addBackgroundToHistory = useCallback((url: string) => {
    if (!userId) {
      return;
    }

    const nextUrl = url.trim();
    if (!nextUrl) {
      return;
    }

    const nextHistory = normalizeHistory([nextUrl, ...backgroundHistory]);
    localStorage.setItem(storageKey, JSON.stringify(nextHistory));
    setRevision((value) => value + 1);
  }, [backgroundHistory, storageKey, userId]);

  const persistCurrentHistory = useCallback(() => {
    if (!userId) {
      return;
    }

    localStorage.setItem(storageKey, JSON.stringify(backgroundHistory));
    setRevision((value) => value + 1);
  }, [backgroundHistory, storageKey, userId]);

  return {
    backgroundHistory,
    addBackgroundToHistory,
    persistCurrentHistory,
  };
}

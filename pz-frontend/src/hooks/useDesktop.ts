import { useEffect, useMemo, useState } from 'react';
import { addApp, deleteApp, getAppsByUserId, updateApp } from '../services/AppService';
import {
  addFamilyMember,
  deleteFamily,
  getFamilyById,
  removeFamilyMember,
  updateUserBackgroundImage,
  updateUserTheme,
} from '../services/api';
import type { DesktopApp } from '../types/Application';
import type { FamilyDetails } from '../types/Family';
import { useBackgroundHistory } from './useBackgroundHistory';

type Theme = 'LIGHT' | 'DARK';

type FamilyMember = FamilyDetails['members'][number];

export function useDesktop(familyId: string, userId: string) {
  const [family, setFamily] = useState<FamilyDetails | null>(null);
  const [apps, setApps] = useState<DesktopApp[]>([]);
  const [selectedAppId, setSelectedAppId] = useState('');
  const [selectedManagedUserId, setSelectedManagedUserId] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [working, setWorking] = useState(false);
  const [uiTheme, setUiTheme] = useState<Theme>('LIGHT');
  const [backgroundUrl, setBackgroundUrl] = useState('');
  const [backgroundInput, setBackgroundInput] = useState('');

  const selectedApp = useMemo(
    () => apps.find((app) => app.id === selectedAppId) ?? null,
    [apps, selectedAppId],
  );

  const currentUser = useMemo(
    () => family?.members.find((member) => member.id === userId) ?? null,
    [family, userId],
  );

  const manageableUsers = useMemo(
    () => family?.members.filter((member) => member.id !== userId) ?? [],
    [family, userId],
  );

  const isAdmin = (currentUser?.role ?? '').toUpperCase() === 'ADMIN';
  const desktopThemeClass = uiTheme === 'DARK' ? 'theme-dark' : 'theme-light';

  const desktopBackgroundStyle = backgroundUrl
    ? {
      backgroundImage: `linear-gradient(rgba(13, 20, 34, 0.36), rgba(13, 20, 34, 0.36)), url(${backgroundUrl})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundAttachment: 'fixed' as const,
    }
    : undefined;

  const {
    backgroundHistory,
    addBackgroundToHistory,
    persistCurrentHistory,
  } = useBackgroundHistory(userId, currentUser?.backgroundImageUrl);

  const updateCurrentUser = (updater: (member: FamilyMember) => FamilyMember) => {
    setFamily((previousFamily) => {
      if (!previousFamily) {
        return previousFamily;
      }

      return {
        ...previousFamily,
        members: previousFamily.members.map((member) => (
          member.id === userId ? updater(member) : member
        )),
      };
    });
  };

  const loadDesktopData = async () => {
    if (!familyId || !userId) {
      setLoading(false);
      setError('Érvenytelen desktop útvonal.');
      return;
    }

    try {
      setLoading(true);
      setError('');

      const [familyData, appData] = await Promise.all([
        getFamilyById(familyId),
        getAppsByUserId(userId),
      ]);

      setFamily(familyData);
      setApps(appData);

      if (!selectedAppId && appData.length > 0) {
        setSelectedAppId(appData[0].id);
      } else if (selectedAppId && !appData.some((app) => app.id === selectedAppId)) {
        setSelectedAppId(appData[0]?.id ?? '');
      }
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'A desktop adatok betoltese sikertelen.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadDesktopData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [familyId, userId]);

  useEffect(() => {
    const savedBackground = currentUser?.backgroundImageUrl ?? '';
    setBackgroundUrl(savedBackground);
    setBackgroundInput(savedBackground);
  }, [currentUser?.backgroundImageUrl]);

  useEffect(() => {
    const resolvedTheme = (currentUser?.theme ?? 'LIGHT').toUpperCase() === 'DARK' ? 'DARK' : 'LIGHT';
    setUiTheme(resolvedTheme);
  }, [currentUser?.theme]);

  const applyBackgroundUrl = async (rawUrl: string) => {
    const nextUrl = rawUrl.trim();
    if (!nextUrl) {
      setError('A hátterkep URL nem lehet üres.');
      return;
    }

    try {
      setWorking(true);
      await updateUserBackgroundImage(userId, nextUrl);
      setBackgroundUrl(nextUrl);
      setBackgroundInput(nextUrl);
      addBackgroundToHistory(nextUrl);
      updateCurrentUser((member) => ({ ...member, backgroundImageUrl: nextUrl }));
      setError('');
    } catch (backgroundError) {
      setError(backgroundError instanceof Error ? backgroundError.message : 'A hátterkep URL mentése sikertelen.');
    } finally {
      setWorking(false);
    }
  };

  const addAppAction = async () => {
    const rawName = window.prompt('Alkalmazás neve:');
    if (!rawName || !rawName.trim()) {
      return;
    }

    try {
      setWorking(true);
      const created = await addApp({ name: rawName.trim(), userId });
      setApps((previousApps) => [...previousApps, created]);
      setSelectedAppId(created.id);
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'Az alkalmazás hozzáadása sikertelen.');
    } finally {
      setWorking(false);
    }
  };

  const modifyAppAction = async () => {
    if (!selectedApp) {
      setError('Először valassz egy alkalmazást.');
      return;
    }

    const rawName = window.prompt('Alkalmazás nevének módosítása:', selectedApp.name);
    if (!rawName || !rawName.trim()) {
      return;
    }

    try {
      setWorking(true);
      const updated = await updateApp(selectedApp.id, { name: rawName.trim(), userId });
      setApps((previousApps) => previousApps.map((app) => (app.id === updated.id ? updated : app)));
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'Az alkalmazás módosítása sikertelen.');
    } finally {
      setWorking(false);
    }
  };

  const deleteAppAction = async () => {
    if (!selectedApp) {
      setError('Először valassz egy alkalmazást.');
      return;
    }

    const confirmed = window.confirm(`Töröljük ezt az alkalmazást: ${selectedApp.name}?`);
    if (!confirmed) {
      return;
    }

    try {
      setWorking(true);
      await deleteApp(selectedApp.id);
      setApps((previousApps) => {
        const nextApps = previousApps.filter((app) => app.id !== selectedApp.id);
        setSelectedAppId(nextApps[0]?.id ?? '');
        return nextApps;
      });
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'Az alkalmazás törlése sikertelen.');
    } finally {
      setWorking(false);
    }
  };

  const addUserAction = async () => {
    const rawName = window.prompt('Új felhasználó neve:');
    if (!rawName || !rawName.trim()) {
      return;
    }

    try {
      setWorking(true);
      const updatedFamily = await addFamilyMember(familyId, rawName.trim());
      setFamily(updatedFamily);
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'A felhasználó hozzáadása sikertelen.');
    } finally {
      setWorking(false);
    }
  };

  const deleteUserAction = async () => {
    if (!selectedManagedUserId) {
      setError('Válassz egy törlendő felhasználót.');
      return;
    }

    const userToDelete = family?.members.find((member) => member.id === selectedManagedUserId);
    const confirmed = window.confirm(`Töröljük ezt a felhasználót: ${userToDelete?.name ?? ''}?`);
    if (!confirmed) {
      return;
    }

    try {
      setWorking(true);
      await removeFamilyMember(familyId, selectedManagedUserId);
      const refreshedFamily = await getFamilyById(familyId);
      setFamily(refreshedFamily);
      setSelectedManagedUserId('');
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'A felhasználó törlése sikertelen.');
    } finally {
      setWorking(false);
    }
  };

  const deleteFamilyAction = async () => {
    const confirmed = window.confirm('Töröljük az egész családot? Ez a művelet nem vonható vissza.');
    if (!confirmed) {
      return false;
    }

    try {
      setWorking(true);
      await deleteFamily(familyId);
      return true;
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'A csalad törlése sikertelen.');
      return false;
    } finally {
      setWorking(false);
    }
  };

  const applyBackgroundFromInputAction = () => {
    void applyBackgroundUrl(backgroundInput);
  };

  const useSavedBackgroundAction = () => {
    if (!backgroundInput.trim()) {
      setError('Először valassz vagy adj meg egy háttérkép URL-t.');
      return;
    }

    void applyBackgroundUrl(backgroundInput);
  };

  const resetBackgroundAction = () => {
    const resetBackground = async () => {
      try {
        setWorking(true);
        await updateUserBackgroundImage(userId, null);
        setBackgroundUrl('');
        setBackgroundInput('');
        persistCurrentHistory();
        updateCurrentUser((member) => ({ ...member, backgroundImageUrl: undefined }));
      } catch (backgroundError) {
        setError(backgroundError instanceof Error ? backgroundError.message : 'A háttérkép visszaállítása sikertelen.');
      } finally {
        setWorking(false);
      }
    };

    void resetBackground();
  };

  const toggleThemeAction = () => {
    const saveTheme = async () => {
      const nextTheme: Theme = uiTheme === 'DARK' ? 'LIGHT' : 'DARK';

      try {
        setWorking(true);
        await updateUserTheme(userId, nextTheme);
        setUiTheme(nextTheme);
        updateCurrentUser((member) => ({ ...member, theme: nextTheme }));
      } catch (themeError) {
        setError(themeError instanceof Error ? themeError.message : 'A témának frissítése sikertelen.');
      } finally {
        setWorking(false);
      }
    };

    void saveTheme();
  };

  return {
    apps,
    backgroundHistory,
    backgroundInput,
    currentUser,
    desktopBackgroundStyle,
    desktopThemeClass,
    error,
    family,
    isAdmin,
    loading,
    manageableUsers,
    selectedApp,
    selectedAppId,
    selectedManagedUserId,
    uiTheme,
    working,
    addAppAction,
    addUserAction,
    applyBackgroundFromInputAction,
    deleteAppAction,
    deleteFamilyAction,
    deleteUserAction,
    modifyAppAction,
    resetBackgroundAction,
    setBackgroundInput,
    setSelectedAppId,
    setSelectedManagedUserId,
    toggleThemeAction,
    useSavedBackgroundAction,
  };
}

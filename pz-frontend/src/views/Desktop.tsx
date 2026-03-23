import { useNavigate, useParams } from 'react-router-dom';
import DesktopAppGrid from '../components/desktop/DesktopAppGrid';
import DesktopNavbar from '../components/desktop/DesktopNavbar';
import { useDesktop } from '../hooks/useDesktop';
import './Desktop.css';

const roleLabel = (role: string) => (role.toUpperCase() === 'ADMIN' ? 'Admin' : 'Felhasznalo');

function Desktop() {
  const navigate = useNavigate();
  const { id, userId } = useParams<{ id: string; userId: string }>();
  const resolvedFamilyId = id ?? '';
  const resolvedUserId = userId ?? '';

  const {
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
  } = useDesktop(resolvedFamilyId, resolvedUserId);

  if (!id || !userId) {
    return (
      <main className="desktop-page">
        <section className="desktop-surface">
          <h1>Ervenytelen desktop utvonal</h1>
          <button className="desktop-secondary-button" type="button" onClick={() => navigate('/')}>
            Vissza
          </button>
        </section>
      </main>
    );
  }

  const handleDeleteFamily = async () => {
    const isDeleted = await deleteFamilyAction();
    if (isDeleted) {
      navigate('/');
    }
  };

  if (loading) {
    return (
      <main className={`desktop-page ${desktopThemeClass}`} style={desktopBackgroundStyle}>
        <section className="desktop-surface">
          <p>Desktop betoltese...</p>
        </section>
      </main>
    );
  }

  return (
    <main className={`desktop-page ${desktopThemeClass}`} style={desktopBackgroundStyle}>
      <section className="desktop-surface">
        <DesktopNavbar
          apps={apps}
          backgroundHistory={backgroundHistory}
          backgroundInput={backgroundInput}
          isAdmin={isAdmin}
          manageableUsers={manageableUsers}
          selectedApp={selectedApp}
          selectedAppId={selectedAppId}
          selectedManagedUserId={selectedManagedUserId}
          uiTheme={uiTheme}
          working={working}
          onAddApp={addAppAction}
          onAddUser={addUserAction}
          onApplyBackgroundFromInput={applyBackgroundFromInputAction}
          onBack={() => navigate(`/family/${id}`)}
          onBackgroundInputChange={setBackgroundInput}
          onDeleteApp={deleteAppAction}
          onDeleteFamily={() => { void handleDeleteFamily(); }}
          onDeleteUser={deleteUserAction}
          onModifyApp={modifyAppAction}
          onResetBackground={resetBackgroundAction}
          onSelectApp={setSelectedAppId}
          onSelectManagedUser={setSelectedManagedUserId}
          onToggleTheme={toggleThemeAction}
          onUseSavedBackground={useSavedBackgroundAction}
        />

        <header className="desktop-header">
          <h1>{family?.name ?? 'Virtualis Desktop'}</h1>
          <p>Bejelentkezve mint {currentUser?.name ?? 'Felhasznalo'} ({roleLabel(currentUser?.role ?? 'USER')})</p>
        </header>

        {error ? <p className="desktop-error">{error}</p> : null}

        <DesktopAppGrid apps={apps} selectedAppId={selectedAppId} onSelectApp={setSelectedAppId} />

        {!apps.length ? <p className="desktop-muted">Meg nincs alkalmazas. Az Alkalmazasok menuben tudsz hozzaadni.</p> : null}
      </section>
    </main>
  );
}

export default Desktop;

import type { DesktopApp } from '../../types/Application';
import type { FamilyMember } from '../../types/Family';

type Theme = 'LIGHT' | 'DARK';

type DesktopNavbarProps = {
  apps: DesktopApp[];
  backgroundHistory: string[];
  backgroundInput: string;
  isAdmin: boolean;
  manageableUsers: FamilyMember[];
  selectedApp: DesktopApp | null;
  selectedAppId: string;
  selectedManagedUserId: string;
  uiTheme: Theme;
  working: boolean;
  onAddApp: () => void;
  onAddUser: () => void;
  onApplyBackgroundFromInput: () => void;
  onBack: () => void;
  onBackgroundInputChange: (value: string) => void;
  onDeleteApp: () => void;
  onDeleteFamily: () => void;
  onDeleteUser: () => void;
  onModifyApp: () => void;
  onResetBackground: () => void;
  onSelectApp: (value: string) => void;
  onSelectManagedUser: (value: string) => void;
  onToggleTheme: () => void;
  onUseSavedBackground: () => void;
};

const roleLabel = (role: string) => (role.toUpperCase() === 'ADMIN' ? 'Admin' : 'Felhasznalo');

function DesktopNavbar({
  apps,
  backgroundHistory,
  backgroundInput,
  isAdmin,
  manageableUsers,
  selectedApp,
  selectedAppId,
  selectedManagedUserId,
  uiTheme,
  working,
  onAddApp,
  onAddUser,
  onApplyBackgroundFromInput,
  onBack,
  onBackgroundInputChange,
  onDeleteApp,
  onDeleteFamily,
  onDeleteUser,
  onModifyApp,
  onResetBackground,
  onSelectApp,
  onSelectManagedUser,
  onToggleTheme,
  onUseSavedBackground,
}: DesktopNavbarProps) {
  return (
    <nav className="desktop-navbar">
      <button type="button" className="desktop-secondary-button" onClick={onBack}>
        Vissza
      </button>

      <div className="desktop-menu-wrap">
        <details className="desktop-menu">
          <summary>Alkalmazások</summary>
          <div className="desktop-menu-content">
            <button type="button" onClick={onAddApp} disabled={working}>Alkalmazás hozzáadása</button>
            <button type="button" onClick={onModifyApp} disabled={working || !selectedApp}>Alkalmazás módosítása</button>
            <button type="button" onClick={onDeleteApp} disabled={working || !selectedApp}>Alkalmazás törlése</button>
            <label htmlFor="app-select">Alkalmazás kiválasztása</label>
            <select
              id="app-select"
              value={selectedAppId}
              onChange={(event) => onSelectApp(event.target.value)}
              disabled={!apps.length}
            >
              {!apps.length ? <option value="">Nincs alkalmazás</option> : null}
              {apps.map((app) => (
                <option key={app.id} value={app.id}>
                  {app.name}
                </option>
              ))}
            </select>
          </div>
        </details>

        {isAdmin ? (
          <details className="desktop-menu">
            <summary>Felhasználók kezelése</summary>
            <div className="desktop-menu-content">
              <button type="button" onClick={onAddUser} disabled={working}>Felhasználó hozzáadása</button>
              <label htmlFor="user-select">Felhasználó törlése</label>
              <select
                id="user-select"
                value={selectedManagedUserId}
                onChange={(event) => onSelectManagedUser(event.target.value)}
                disabled={!manageableUsers.length}
              >
                <option value="">Felhasználó kiválasztása</option>
                {manageableUsers.map((member) => (
                  <option key={member.id} value={member.id}>
                    {member.name} ({roleLabel(member.role)})
                  </option>
                ))}
              </select>
              <button
                type="button"
                onClick={onDeleteUser}
                disabled={working || !selectedManagedUserId}
              >
                Felhasználó törlése
              </button>
              <button
                type="button"
                className="desktop-danger"
                onClick={onDeleteFamily}
                disabled={working}
              >
                Család törlése
              </button>
            </div>
          </details>
        ) : null}

        <details className="desktop-menu">
          <summary>Hátter</summary>
          <div className="desktop-menu-content">
            <label htmlFor="background-url">Kép URL</label>
            <input
              id="background-url"
              className="desktop-menu-input"
              type="text"
              placeholder="https://..."
              value={backgroundInput}
              onChange={(event) => onBackgroundInputChange(event.target.value)}
            />
            <button type="button" onClick={onApplyBackgroundFromInput}>
              URL alkalmazása
            </button>
            <label htmlFor="saved-background-select">Mentett hátterek</label>
            <select
              id="saved-background-select"
              value={backgroundInput}
              onChange={(event) => onBackgroundInputChange(event.target.value)}
              disabled={!backgroundHistory.length}
            >
              {!backgroundHistory.length ? <option value="">Nincs mentett hátter</option> : null}
              {backgroundHistory.map((savedBackground) => (
                <option key={savedBackground} value={savedBackground}>
                  {savedBackground}
                </option>
              ))}
            </select>
            <button type="button" onClick={onUseSavedBackground} disabled={!backgroundHistory.length}>
              Mentett használata
            </button>
            <button type="button" onClick={onResetBackground}>
              átter visszaállítása
            </button>
          </div>
        </details>

        <button type="button" className="desktop-secondary-button" onClick={onToggleTheme} disabled={working}>
          Váltas ide: {uiTheme === 'DARK' ? 'Vilagos' : 'Sotet'}
        </button>
      </div>
    </nav>
  );
}

export default DesktopNavbar;

import type { DesktopApp } from '../../types/Application';

type DesktopAppGridProps = {
  apps: DesktopApp[];
  selectedAppId: string;
  onSelectApp: (appId: string) => void;
};

function DesktopAppGrid({ apps, selectedAppId, onSelectApp }: DesktopAppGridProps) {
  return (
    <section className="desktop-app-grid" aria-label="Alkalmazasok racsa">
      {apps.map((app) => (
        <button
          key={app.id}
          type="button"
          className={`desktop-app-tile ${selectedAppId === app.id ? 'desktop-app-tile-active' : ''}`}
          onClick={() => onSelectApp(app.id)}
        >
          <span className="desktop-app-glyph">{app.name.charAt(0).toUpperCase()}</span>
          <span className="desktop-app-label">{app.name}</span>
        </button>
      ))}
    </section>
  );
}

export default DesktopAppGrid;

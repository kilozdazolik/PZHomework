import './LandingPage.css'
import { useState } from 'react';
import { createFamily } from '../services/api';

type Mode = null | 'existing' | 'new';

function LandingPage() {
  const [mode, setMode] = useState<Mode>(null);
  const [familyId, setFamilyId] = useState('');
  const [lastName, setLastName] = useState('');
  const [lastNameError, setLastNameError] = useState('');

  const validateFamilyName = (name: string): string => {
    const trimmed = name.trim();

    if (trimmed.length < 2) {
      return 'A vezetéknév túl rövid (minimum 2 karakter).';
    }

    if (trimmed.length > 40) {
      return 'A vezetéknév túl hosszú (maximum 50 karakter).';
    }

    if (!/^[\p{L}\s'-]+$/u.test(trimmed)) {
      return 'A vezetéknév csak betűket, szóközöket, aposztrofokat és kötőjeleket tartalmazhat.';
    }

    return '';
  };

  const handleExistingSubmit = (e: React.SubmitEvent) => {
    e.preventDefault();
	    console.log('Keresett ID:', familyId);
  };

  const handleNewSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault();

    const validationError = validateFamilyName(lastName);
    if (validationError) {
      setLastNameError(validationError);
      return;
    }

    try {
      const normalizedName = lastName.trim();
      await createFamily(normalizedName);
      setLastNameError('');
      console.log('Csalad letrehozva a nev alapjan:', normalizedName);

    } catch (err) {
      setLastNameError('Nem sikerült létrehozni a családot. Próbáld újra.');
      console.error('Varatlan hiba:', err);
    }
  };

  return (
    <main className="landing-page">
      <section className="landing-card" aria-labelledby="landing-title">
        <h1 id="landing-title" className="landing-title">
          Üdvözöllek
        </h1>
        <p className="landing-subtitle">Kérlek válassz egy lehetőséget</p>

        <div className="landing-actions">
          <button
            type="button"
            className={`landing-button landing-button-primary ${mode === 'existing' ? 'active' : ''}`}
            onClick={() => setMode('existing')}
          >
            Van már családom
          </button>
          <button
            type="button"
            className={`landing-button landing-button-secondary ${mode === 'new' ? 'active' : ''}`}
            onClick={() => setMode('new')}
          >
            Új családot hozok létre
          </button>
        </div>

        {mode === 'existing' && (
          <form onSubmit={handleExistingSubmit} className="landing-form">
            <label htmlFor="familyId" className="landing-kicker">
              Kérlek írd be az egyedi azonosítód
            </label>
            <input
              id="familyId"
              type="text"
              value={familyId}
              onChange={(e) => setFamilyId(e.target.value)}
              placeholder="36 karakter hosszú egyedi azonosító"
              required
            />
            <button type="submit" className="landing-button landing-button-primary">
              Belépés
            </button>
          </form>
        )}

        {mode === 'new' && (
          <form onSubmit={handleNewSubmit} className="landing-form">
            <label htmlFor="lastName" className="landing-kicker">
              Kérlek írd be a vezetékneved
            </label>
            <input
              id="lastName"
              type="text"
              value={lastName}
              onChange={(e) => {
                const nextValue = e.target.value;
                setLastName(nextValue);
                setLastNameError(validateFamilyName(nextValue));
              }}
              placeholder="pl. Kovács"
              className={lastNameError ? 'input-error' : ''}
              aria-invalid={Boolean(lastNameError)}
              aria-describedby={lastNameError ? 'lastName-error' : undefined}
              required
            />
            {lastNameError && (
              <p id="lastName-error" className="landing-error" role="alert">
                {lastNameError}
              </p>
            )}
            <button type="submit" className="landing-button landing-button-primary">
              Család létrehozása
            </button>
          </form>
        )}
      </section>
    </main>
  );
}

export default LandingPage;
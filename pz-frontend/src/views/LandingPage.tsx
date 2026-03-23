import './LandingPage.css'
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createFamily } from '../services/api';

type Mode = null | 'existing' | 'new';

function LandingPage() {
  const navigate = useNavigate();
  const [mode, setMode] = useState<Mode>(null);
  const [familyId, setFamilyId] = useState('');
  const [lastName, setLastName] = useState('');
  const [lastNameError, setLastNameError] = useState('');
  const [familyIdError, setFamilyIdError] = useState('');

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

  const handleExistingSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const trimmedFamilyId = familyId.trim();
    if (!trimmedFamilyId) {
      setFamilyIdError('Add meg a csaladi azonositot.');
      return;
    }

    setFamilyIdError('');
    navigate(`/family/${trimmedFamilyId}`);
  };

  const handleNewSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const validationError = validateFamilyName(lastName);
    if (validationError) {
      setLastNameError(validationError);
      return;
    }

    try {
      const normalizedName = lastName.trim();
      const createdFamily = await createFamily(normalizedName);
      setLastNameError('');
      navigate(`/family/${createdFamily.id}`);

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
              onChange={(e) => {
                setFamilyId(e.target.value);
                if (familyIdError) {
                  setFamilyIdError('');
                }
              }}
              placeholder="36 karakter hosszú egyedi azonosító"
              className={familyIdError ? 'input-error' : ''}
              aria-invalid={Boolean(familyIdError)}
              aria-describedby={familyIdError ? 'familyId-error' : undefined}
              required
            />
            {familyIdError && (
              <p id="familyId-error" className="landing-error" role="alert">
                {familyIdError}
              </p>
            )}
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
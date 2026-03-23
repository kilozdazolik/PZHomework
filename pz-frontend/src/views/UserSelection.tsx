import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { addFamilyMember, getFamilyById } from '../services/api';
import type { FamilyDetails } from '../types/Family';
import './UserSelection.css';

const validateUserName = (name: string): string => {
  const trimmed = name.trim();

  if (trimmed.length < 2) {
    return 'A név túl rövid (minimum 2 karakter).';
  }

  if (trimmed.length > 50) {
    return 'A név túl hosszú (maximum 50 karakter).';
  }

  if (!/^[\p{L}\s'-]+$/u.test(trimmed)) {
    return 'A név csak betűket, szóközöket, aposztrofokat és kötőjeleket tartalmazhat.';
  }

  return '';
};

function UserSelection() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  const [family, setFamily] = useState<FamilyDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [creatingMember, setCreatingMember] = useState(false);

  const members = useMemo(() => family?.members ?? [], [family]);

  useEffect(() => {
    if (!id) {
      setLoading(false);
      setError('Hiányzó családi azonosító');
      return;
    }

    let cancelled = false;

    const loadFamily = async () => {
      setLoading(true);
      setError('');

      try {
        const data = await getFamilyById(id);
        if (!cancelled) {
          setFamily(data);
        }
      } catch (loadError) {
        if (!cancelled) {
          setError(loadError instanceof Error ? loadError.message : 'Nem sikerült betölteni a családot.');
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    void loadFamily();

    return () => {
      cancelled = true;
    };
  }, [id]);

  const handleAddMember = async () => {
    if (!id) {
      return;
    }

    const rawName = window.prompt('Add meg az uj profil nevet:');
    if (rawName === null) {
      return;
    }

    const validationMessage = validateUserName(rawName);
    if (validationMessage) {
      setError(validationMessage);
      return;
    }

    setCreatingMember(true);
    setError('');

    try {
      const updatedFamily = await addFamilyMember(id, rawName.trim());
      setFamily(updatedFamily);
    } catch (createError) {
      setError(createError instanceof Error ? createError.message : 'Nem sikerült új profilt létrehozni.');
    } finally {
      setCreatingMember(false);
    }
  };

  if (loading) {
    return (
      <main className="user-selection-page">
        <section className="user-selection-card">
          <p>Csalad betoltese...</p>
        </section>
      </main>
    );
  }

  if (error && !family) {
    return (
      <main className="user-selection-page">
        <section className="user-selection-card">
          <p className="user-selection-error">{error}</p>
          <button className="member-add member-add-large" type="button" onClick={() => navigate('/')}>
            Vissza
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="user-selection-page">
      <section className="user-selection-card" aria-labelledby="family-title">
        <h1 id="family-title">{family?.name ?? 'Csalad'}</h1>
        <p className="user-selection-subtitle">Válassz profilt vagy adj hozzá újat</p>

        {error && <p className="user-selection-error" role="alert">{error}</p>}

        {members.length === 0 ? (
          <div className="empty-family-wrapper">
            <button
              type="button"
              className="member-add member-add-large"
              onClick={handleAddMember}
              disabled={creatingMember}
              aria-label="Új profil hozzáadása"
            >
              +
            </button>
          </div>
        ) : (
          <div className="member-grid">
            {members.map((member) => (
              <button
                key={member.id}
                type="button"
                className="member-card"
                onClick={() => navigate(`/family/${id}/user/${member.id}`)}
              >
                <span className="member-name">{member.name}</span>
                <span className="member-role">{member.role}</span>
              </button>
            ))}

            <button
              type="button"
              className="member-add"
              onClick={handleAddMember}
              disabled={creatingMember}
              aria-label="Új profil hozzáadása"
            >
              +
            </button>
          </div>
        )}
      </section>
    </main>
  );
}

export default UserSelection;

import { useNavigate, useParams } from 'react-router-dom';
import './LandingPage.css';

function Desktop() {
  const navigate = useNavigate();
  const { id, userId } = useParams<{ id: string; userId: string }>();

  return (
    <main className="landing-page">
      <section className="landing-card">
        <h1>Desktop placeholder</h1>
        <p>Csalad azonosito: {id}</p>
        <p>Felhasznalo azonosito: {userId}</p>
        <button className="landing-button landing-button-secondary" type="button" onClick={() => navigate(`/family/${id}`)}>
          Vissza
        </button>
      </section>
    </main>
  );
}

export default Desktop;

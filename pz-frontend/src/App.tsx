import { BrowserRouter, Route, Routes } from 'react-router-dom';
import LandingPage from './views/LandingPage';
import UserSelection from './views/UserSelection';
import Desktop from './views/Desktop';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/family/:id" element={<UserSelection />} />
        <Route path="/family/:id/user/:userId" element={<Desktop />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

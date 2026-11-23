import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import Office from "./Office";
import Offices from "./Offices";

function App() {
  return (
    <BrowserRouter>
      <nav style={{ margin: "16px 0" }}>
        <Link to="/">Save office</Link> | <Link to="/offices">Available offices</Link>
      </nav>

      <Routes>
        <Route path="/" element={<Office />} />
        <Route path="/offices" element={<Offices />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
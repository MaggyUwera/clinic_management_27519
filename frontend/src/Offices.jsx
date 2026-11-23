import { useEffect, useState } from "react";
import axios from "axios";

const API_URL = "http://localhost:1014/api/office";

// Styles
const box = {
  maxWidth: "720px",
  margin: "24px auto",
  padding: "24px 32px 32px",
  border: "2px solid #555",
  borderRadius: "8px",
  textAlign: "center",
};
const table = { width: "100%", borderCollapse: "collapse", marginTop: "16px" };
const cell = { border: "1px solid #555", padding: "8px 12px", textAlign: "center" };
const headerCell = { ...cell, fontWeight: "bold" };

function Offices() {
  const [offices, setOffices] = useState([]);
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");

  // Runs when the page opens, and again every time the search text changes
  useEffect(() => {
    loadOffices();
  }, [search]);

  async function loadOffices() {
    try {
      let response;
      if (search.trim() === "") {
        // No search text: fetch all offices
        response = await axios.get(`${API_URL}/all`);
      } else {
        // Search by office name or office number: /search?search=...
        response = await axios.get(`${API_URL}/search`, { params: { search } });
      }
      setOffices(response.data);
      setError("");
    } catch (err) {
      console.error(err);
      setOffices([]);
      setError("Could not load offices: " + err.message);
    }
  }

  async function deleteOffice(id) {
    if (!window.confirm("Are you sure you want to delete this office?")) return;

    try {
      const response = await axios.delete(`${API_URL}/delete/${id}`);
      alert(response.data); // e.g. "Office is deleted Successfully"
      loadOffices();
    } catch (err) {
      console.error(err);
      const data = err.response ? err.response.data : null;
      alert(typeof data === "string" ? data : "Could not delete office: " + err.message);
    }
  }

  return (
    <div style={box}>
      <h2>Available Offices</h2>

      <div>
        <label>Search: </label>
        <input
          type="text"
          placeholder="Office name or number"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <table style={table}>
        <thead>
          <tr>
            <th style={headerCell}>Id</th>
            <th style={headerCell}>Name</th>
            <th style={headerCell}>Office Number</th>
            <th style={headerCell}>Action</th>
          </tr>
        </thead>
        <tbody>
          {offices.length === 0 ? (
            <tr>
              <td style={cell} colSpan="4">No offices found</td>
            </tr>
          ) : (
            offices.map((office, index) => (
              <tr key={office.id}>
                {/* Row number 1, 2, 3... (the real id is still used for Delete) */}
                <td style={cell}>{index + 1}</td>
                <td style={cell}>{office.name}</td>
                <td style={cell}>{office.officeNumber}</td>
                <td style={cell}>
                  {/* Update will be implemented in the next class */}
                  <button style={{ marginRight: "8px" }}>Update</button>
                  <button onClick={() => deleteOffice(office.id)}>Delete</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default Offices;
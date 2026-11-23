import { useState } from "react";
import axios from "axios";

// Labels get the same width so the two inputs line up
const labelStyle = { display: "inline-block", width: "140px" };

function Office() {
  const [officeName, setOfficeName] = useState("");
  const [officeNumber, setOfficeNumber] = useState("");

  async function saveOffice(e) {
    e.preventDefault();

    try {
      await axios.post("http://localhost:1014/api/office/save", {
        name: officeName,
        officeNumber: Number(officeNumber),
      });
      alert("Office saved successfully!");
      setOfficeName("");
      setOfficeNumber("");
    } catch (error) {
      console.error(error);
      if (error.response) {
        // The backend answered with an error message (e.g. office number already exists)
        const data = error.response.data;
        alert(typeof data === "string" ? data : "Error " + error.response.status);
      } else {
        // No answer: backend not running, wrong port, or blocked by CORS
        alert("Cannot reach the backend: " + error.message);
      }
    }
  }

  return (
    // Keep everything on the left, so the browser pop-up (top centre) doesn't cover it
    <div style={{ textAlign: "left", paddingLeft: "8px" }}>
      <h1>Save office</h1>

      <form onSubmit={saveOffice}>
        <div>
          <label style={labelStyle}>Office Name:</label>
          <input
            type="text"
            style={{ width: "160px" }}
            value={officeName}
            onChange={(e) => setOfficeName(e.target.value)}
          />
        </div>

        <div>
          <label style={labelStyle}>Office Number:</label>
          <input
            type="number"
            style={{ width: "160px" }}
            value={officeNumber}
            onChange={(e) => setOfficeNumber(e.target.value)}
          />
        </div>

        <button type="submit" style={{ marginTop: "8px" }}>SAVE OFFICE</button>
      </form>
    </div>
  );
}

export default Office;

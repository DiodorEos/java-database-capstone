import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { openModal } from "./modals.js";
import { showBookingOverlay } from "../booking.js";
export function createDoctorCard(doctor) {
  const card = document.createElement("article"); card.className = "doctor-card";
  const badge = document.createElement("div"); badge.className = "doctor-avatar";
  badge.textContent = String(doctor.name || "Doctor").replace(/^Dr\.\s*/i,"").split(/\s+/).slice(0,2).map(s => s[0]).join("");
  const info = document.createElement("div"); info.className = "doctor-info";
  for (const [tag, text, cls] of [["p", doctor.specialty, "eyebrow"], ["h2", doctor.name, ""], ["p", doctor.email, "muted"], ["p", (doctor.availableTimes || []).join(" · ") || "No times listed", "availability"]]) {
    const el = document.createElement(tag); el.textContent = text || ""; el.className = cls; info.append(el);
  }
  const actions = document.createElement("div"); actions.className = "card-actions";
  const button = document.createElement("button"); button.type = "button";
  const role = localStorage.getItem("userRole"); button.textContent = role === "admin" ? "Delete doctor" : "Book appointment";
  if (role === "admin") button.className = "danger secondary";
  const status = document.createElement("p"); status.setAttribute("role","status");
  button.addEventListener("click", async event => {
    if (role !== "admin" && role !== "loggedPatient") { openModal("patientLogin"); return; }
    if (role === "admin" && !window.confirm("Delete this doctor's profile? Linked appointments may prevent deletion.")) return;
    button.disabled = true; status.textContent = "";
    try {
      const token = localStorage.getItem("token"); if (!token) throw new Error("Please sign in again.");
      if (role === "admin") {
        const result = await deleteDoctor(doctor.id, token);
        if (!result.success) throw new Error(result.message);
        document.dispatchEvent(new CustomEvent("clinic:doctors-changed"));
      } else {
        const patient = await getPatientData(token); if (!patient) throw new Error("Unable to load your patient profile.");
        showBookingOverlay(event, doctor, patient);
      }
    } catch (error) { status.textContent = error.message; }
    finally { button.disabled = false; }
  });
  actions.append(button, status); card.append(badge, info, actions); return card;
}

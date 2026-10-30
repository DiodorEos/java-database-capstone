import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";
const body = document.getElementById("patientTableBody"), picker = document.getElementById("datePicker");
function today() { const d = new Date(); return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`; }
let sequence = 0, timer; picker.value = today();
function message(text) { const tr = document.createElement("tr"), td = document.createElement("td"); td.colSpan = 7; td.className = "empty-state"; td.textContent = text; tr.append(td); body.replaceChildren(tr); }
async function loadAppointments() {
  const current = ++sequence;
  if (!picker.value) { message("Choose an appointment date."); return; }
  body.setAttribute("aria-busy","true");
  try {
    const data = await getAllAppointments(picker.value, document.getElementById("searchBar").value.trim() || null, localStorage.getItem("token"));
    if (current !== sequence) return;
    const appointments = Array.isArray(data) ? data : data.appointments;
    if (!Array.isArray(appointments)) throw new Error("Unexpected appointment response.");
    document.getElementById("resultCount").textContent = `${appointments.length} appointments`;
    if (!appointments.length) { message("No appointments found for the selected date and name."); return; }
    body.replaceChildren(...appointments.map(a => createPatientRow(a.patient || {id: a.patientId, name: a.patientName, phone: a.patientPhone, email: a.patientEmail}, a.id, a.doctor?.id || a.doctorId, a)));
  } catch (error) { if (current === sequence) { message(error.message); document.getElementById("resultCount").textContent = ""; } }
  finally { if (current === sequence) body.setAttribute("aria-busy","false"); }
}
document.getElementById("searchBar").addEventListener("input", () => { ++sequence; clearTimeout(timer); timer = setTimeout(loadAppointments,250); });
picker.addEventListener("change",loadAppointments);
document.getElementById("todayButton").addEventListener("click", () => { picker.value = today(); loadAppointments(); });
loadAppointments();

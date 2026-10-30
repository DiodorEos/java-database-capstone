import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
export function renderDoctorCards(doctors) {
  const host = document.getElementById("content"); host.replaceChildren();
  const count = document.getElementById("resultCount"); if (count) count.textContent = `${doctors.length} doctor${doctors.length === 1 ? "" : "s"}`;
  if (!doctors.length) { const p = document.createElement("p"); p.className = "empty-state"; p.textContent = "No doctors found with these filters."; host.append(p); }
  else host.append(...doctors.map(createDoctorCard));
}
export function initializeDirectory() {
  const host = document.getElementById("content"); if (!host) return;
  let sequence = 0, timer;
  async function load() {
    const current = ++sequence; host.setAttribute("aria-busy", "true");
    const name = document.getElementById("searchBar").value.trim();
    const time = document.getElementById("filterTime").value;
    const specialty = document.getElementById("filterSpecialty").value;
    try {
      const doctors = name || time || specialty ? (await filterDoctors(name, time, specialty)).doctors : await getDoctors();
      if (current === sequence) renderDoctorCards(doctors);
    } catch (error) {
      if (current !== sequence) return;
      const p = document.createElement("p"); p.className = "empty-state error"; p.setAttribute("role","alert"); p.textContent = error.message;
      const retry = document.createElement("button"); retry.textContent = "Try again"; retry.addEventListener("click", load);
      host.replaceChildren(p, retry);
      const count = document.getElementById("resultCount"); if (count) count.textContent = "";
    } finally { if (current === sequence) host.setAttribute("aria-busy", "false"); }
  }
  document.getElementById("searchBar").addEventListener("input", () => { ++sequence; clearTimeout(timer); timer = setTimeout(load, 250); });
  for (const id of ["filterTime", "filterSpecialty"]) document.getElementById(id).addEventListener("change", load);
  document.addEventListener("clinic:doctors-changed", load); load();
}

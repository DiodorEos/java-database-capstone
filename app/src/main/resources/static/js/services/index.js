import { openModal } from "../components/modals.js";
import { request } from "./http.js";
async function login(role, form) {
  const identity = role === "admin" ? "username" : "email";
  const data = await request(role === "admin" ? "/admin" : "/doctor/login", {
    method: "POST", body: JSON.stringify({[identity]: form.elements[identity].value.trim(), password: form.elements.password.value})
  });
  if (typeof data.token !== "string" || !data.token) throw new Error("The clinic did not return a login token.");
  localStorage.setItem("token", data.token); window.selectRole(role);
}
window.adminLoginHandler = form => login("admin", form);
window.doctorLoginHandler = form => login("doctor", form);
document.getElementById("adminLogin")?.addEventListener("click", () => openModal("adminLogin"));
document.getElementById("doctorLogin")?.addEventListener("click", () => openModal("doctorLogin"));
document.getElementById("patientRole")?.addEventListener("click", () => { window.clearClinicSession(); window.selectRole("patient"); });

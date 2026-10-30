import { closeModal, openModal } from "./components/modals.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";
import { initializeDirectory, renderDoctorCards } from "./directory.js";
export { renderDoctorCards };
if (document.body.dataset.page === "patient" && localStorage.getItem("userRole") !== "loggedPatient") localStorage.setItem("userRole", "patient");
window.signupPatient = async form => {
  const values = Object.fromEntries(new FormData(form)); values.name = values.name.trim(); values.email = values.email.trim();
  const result = await patientSignup(values); if (!result.success) throw new Error(result.message);
  closeModal(); document.getElementById("pageStatus").textContent = "Account created. You can now log in."; openModal("patientLogin");
};
window.loginPatient = async form => {
  const response = await patientLogin({email: form.elements.email.value.trim(), password: form.elements.password.value});
  if (!response.ok) throw new Error(response.status === 401 ? "Invalid credentials." : "Unable to log in. Please try again.");
  let data; try { data = await response.json(); } catch { throw new Error("Invalid login response."); }
  if (typeof data.token !== "string" || !data.token) throw new Error("The clinic did not return a login token.");
  localStorage.setItem("token", data.token); window.selectRole("loggedPatient");
};
initializeDirectory();

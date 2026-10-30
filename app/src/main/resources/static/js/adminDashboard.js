import { closeModal } from "./components/modals.js";
import { saveDoctor } from "./services/doctorServices.js";
import { initializeDirectory, renderDoctorCards } from "./directory.js";
export { renderDoctorCards };
window.adminAddDoctor = async form => {
  const token = localStorage.getItem("token"); if (!token) throw new Error("Please sign in again.");
  const values = new FormData(form);
  const availableTimes = values.getAll("availability"); if (!availableTimes.length) throw new Error("Select at least one consultation hour.");
  const doctor = {name: values.get("doctorName").trim(), specialty: values.get("specialization").trim(), email: values.get("doctorEmail").trim(), password: values.get("doctorPassword"), phone: values.get("doctorPhone"), availableTimes};
  const result = await saveDoctor(doctor, token); if (!result.success) throw new Error(result.message);
  closeModal(); document.dispatchEvent(new CustomEvent("clinic:doctors-changed"));
  document.getElementById("pageStatus").textContent = "Doctor added successfully.";
};
initializeDirectory();

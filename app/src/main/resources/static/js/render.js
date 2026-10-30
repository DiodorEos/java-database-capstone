function selectRole(role) {
  const token = localStorage.getItem("token");
  if (role === "patient") { setRole(role); window.location.assign("/pages/patientDashboard.html"); return; }
  if (!token || !["admin", "doctor", "loggedPatient"].includes(role)) return;
  setRole(role);
  const path = role === "loggedPatient" ? "/pages/loggedPatientDashboard.html" : `/${role}Dashboard/${encodeURIComponent(token)}`;
  window.location.assign(path);
}
function renderContent() {
  const role = getRole();
  if (["admin", "doctor", "loggedPatient"].includes(role) && !localStorage.getItem("token")) {
    clearClinicSession(); window.location.replace("/"); return false;
  }
  return true;
}

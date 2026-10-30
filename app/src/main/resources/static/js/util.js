function setRole(role) { localStorage.setItem("userRole", role); }
function getRole() { return localStorage.getItem("userRole"); }
function clearClinicSession() {
  localStorage.removeItem("token"); localStorage.removeItem("userRole");
}
function logout() { clearClinicSession(); window.location.replace("/"); }
function logoutPatient() { clearClinicSession(); setRole("patient"); window.location.replace("/pages/patientDashboard.html"); }

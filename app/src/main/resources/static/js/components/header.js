function renderHeader() {
  const host = document.getElementById("header"); if (!host) return;
  const header = document.createElement("header"); header.className = "header";
  const brand = document.createElement("a"); brand.href = "/"; brand.className = "brand"; brand.textContent = "SmartCare";
  const mark = document.createElement("span"); mark.className = "brand-mark"; mark.textContent = "+"; brand.prepend(mark); header.append(brand);
  const nav = document.createElement("nav"); nav.setAttribute("aria-label", "Main navigation");
  const addButton = (id, text, action) => { const b = document.createElement("button"); b.id = id; b.type = "button"; b.textContent = text; b.addEventListener("click", action); nav.append(b); };
  const modal = type => document.dispatchEvent(new CustomEvent("clinic:modal", {detail: type}));
  const page = document.body.dataset.page;
  const role = localStorage.getItem("userRole");
  if (page === "home") {
    const a = document.createElement("a"); a.href = "/pages/patientDashboard.html"; a.textContent = "Find a doctor"; nav.append(a);
  } else if (page === "admin" || role === "admin") {
    addButton("addDocBtn", "Add Doctor", () => modal("addDoctor")); addButton("logout", "Log out", logout);
  } else if (page === "doctor" || role === "doctor") {
    addButton("homeBtn", "My appointments", () => selectRole("doctor")); addButton("logout", "Log out", logout);
  } else if (role === "loggedPatient") {
    addButton("homeBtn", "Find a doctor", () => selectRole("loggedPatient"));
    const a = document.createElement("a"); a.href = "/pages/patientAppointments.html"; a.textContent = "My appointments"; nav.append(a);
    addButton("logout", "Log out", logoutPatient);
  } else {
    addButton("patientLogin", "Log in", () => modal("patientLogin")); addButton("patientSignup", "Sign up", () => modal("patientSignup"));
  }
  header.append(nav); host.replaceChildren(header);
}
renderHeader();

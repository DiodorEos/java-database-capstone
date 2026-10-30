let previousFocus;
export function closeModal() {
  const modal = document.getElementById("modal"); if (!modal) return;
  modal.hidden = true; modal.style.display = "none"; document.body.classList.remove("modal-open"); previousFocus?.focus();
}
const schemas = {
  adminLogin: ["Admin login", "adminLoginHandler", [["username","Username","text"],["password","Password","password"]]],
  doctorLogin: ["Doctor login", "doctorLoginHandler", [["email","Email","email"],["password","Password","password"]]],
  patientLogin: ["Patient login", "loginPatient", [["email","Email","email"],["password","Password","password"]]],
  patientSignup: ["Create a patient account", "signupPatient", [["name","Full name","text",3,100],["email","Email","email"],["password","Password","password",6,72],["phone","Phone (10 digits)","tel"],["address","Address","text",1,255]]],
  addDoctor: ["Add Doctor", "adminAddDoctor", [["doctorName","Full name","text",3,100],["specialization","Specialty","text",3,50],["doctorEmail","Email","email"],["doctorPassword","Temporary password","password",6,72],["doctorPhone","Phone (10 digits)","tel"]]]
};
export function openModal(type) {
  const schema = schemas[type]; const modal = document.getElementById("modal"); if (!schema || !modal) return;
  previousFocus = document.activeElement;
  const [title, handler, fields] = schema;
  const body = document.getElementById("modal-body"); body.replaceChildren();
  const heading = document.createElement("h2"); heading.id = "modal-title"; heading.textContent = title;
  const form = document.createElement("form"); form.id = "clinicForm";
  for (const [id, labelText, inputType, min, max] of fields) {
    const label = document.createElement("label"); label.htmlFor = id; label.textContent = labelText;
    const input = document.createElement("input"); input.id = id; input.name = id; input.type = inputType; input.required = true;
    if (min) input.minLength = min; if (max) input.maxLength = max;
    if (inputType === "email") { input.maxLength = 254; input.autocomplete = "email"; }
    if (inputType === "password") input.autocomplete = type.endsWith("Login") ? "current-password" : "new-password";
    if (inputType === "tel") { input.pattern = "[0-9]{10}"; input.maxLength = 10; input.inputMode = "numeric"; }
    form.append(label, input);
  }
  if (type === "addDoctor") {
    const fieldset = document.createElement("fieldset"); const legend = document.createElement("legend"); legend.textContent = "Available consultation hours"; fieldset.append(legend);
    const group = document.createElement("div"); group.className = "checkbox-group";
    for (let hour = 9; hour < 17; hour++) {
      const label = document.createElement("label"), input = document.createElement("input"); input.type = "checkbox"; input.name = "availability";
      input.value = `${String(hour).padStart(2,"0")}:00-${String(hour+1).padStart(2,"0")}:00`; label.append(input, document.createTextNode(input.value)); group.append(label);
    }
    fieldset.append(group); form.append(fieldset);
  }
  const error = document.createElement("p"); error.className = "form-error"; error.setAttribute("role","alert");
  const submit = document.createElement("button"); submit.type = "submit"; submit.textContent = type === "addDoctor" ? "Save Doctor" : type === "patientSignup" ? "Create account" : "Log in";
  form.append(error, submit); body.append(heading, form);
  form.addEventListener("submit", async event => {
    event.preventDefault(); if (!form.reportValidity() || submit.disabled) return;
    error.textContent = ""; submit.disabled = true;
    try {
      if (typeof window[handler] !== "function") throw new Error("This action is unavailable on this page.");
      await window[handler](form);
    } catch (err) { error.textContent = err.message || "Unable to complete this action."; }
    finally { submit.disabled = false; }
  });
  modal.hidden = false; modal.style.display = "flex"; document.body.classList.add("modal-open");
  document.getElementById("closeModal").onclick = closeModal;
  modal.onclick = event => { if (event.target === modal) closeModal(); };
  modal.onkeydown = event => {
    if (event.key === "Escape") closeModal();
    if (event.key === "Tab") {
      const nodes = [...modal.querySelectorAll("button, input, select, textarea, a[href]")].filter(n => !n.disabled);
      const first = nodes[0], last = nodes.at(-1);
      if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
      else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
    }
  };
  form.querySelector("input")?.focus();
}
document.addEventListener("clinic:modal", event => openModal(event.detail));

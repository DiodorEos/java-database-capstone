export function createPatientRow(patient, appointmentId, doctorId, appointment = {}) {
  const tr = document.createElement("tr");
  const cell = text => { const td = document.createElement("td"); td.textContent = text ?? "—"; tr.append(td); return td; };
  const identity = cell(""); const record = document.createElement("a");
  record.textContent = String(patient.id); record.href = "/pages/patientRecord.html?" + new URLSearchParams({id: patient.id, doctorId}); identity.append(record);
  cell(patient.name); cell(patient.phone); cell(patient.email);
  cell(appointment.appointmentTime || [appointment.appointmentDate, appointment.appointmentTimeOnly].filter(Boolean).join(" "));
  cell(appointment.status === 1 ? "Completed" : "Scheduled");
  const actions = cell(""); const link = document.createElement("a"); link.className = "button secondary";
  link.textContent = "Prescription"; link.href = "/pages/addPrescription.html?" + new URLSearchParams({appointmentId, patientName: patient.name}); actions.append(link);
  return tr;
}

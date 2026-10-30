import { bookAppointment } from "./services/appointmentRecordService.js";
export function showBookingOverlay(event, doctor, patient) {
  const dialog = document.createElement("dialog"); dialog.className = "booking-dialog";
  const form = document.createElement("form"); const heading = document.createElement("h2"); heading.textContent = "Book an appointment";
  const summary = document.createElement("p"); summary.textContent = `${patient.name} · ${doctor.name} · One-hour consultation`;
  const dateLabel = document.createElement("label"); dateLabel.textContent = "Date";
  const date = document.createElement("input"); date.type = "date"; date.required = true;
  const today = new Date(); date.min = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,"0")}-${String(today.getDate()).padStart(2,"0")}`; dateLabel.append(date);
  const timeLabel = document.createElement("label"); timeLabel.textContent = "Consultation hour";
  const time = document.createElement("select"); time.required = true; time.append(new Option("Choose a time", ""));
  for (const slot of doctor.availableTimes || []) time.append(new Option(slot, slot)); timeLabel.append(time);
  const note = document.createElement("p"); note.className = "muted"; note.textContent = "Availability is confirmed when you submit your booking.";
  const error = document.createElement("p"); error.setAttribute("role","alert");
  const submit = document.createElement("button"); submit.textContent = "Confirm booking"; submit.type = "submit";
  const cancel = document.createElement("button"); cancel.type = "button"; cancel.className = "secondary"; cancel.textContent = "Cancel"; cancel.onclick = () => dialog.close();
  form.append(heading, summary, dateLabel, timeLabel, note, error, submit, cancel); dialog.append(form); document.body.append(dialog);
  dialog.addEventListener("close", () => dialog.remove()); dialog.showModal();
  form.onsubmit = async e => {
    e.preventDefault(); if (!form.reportValidity() || submit.disabled) return;
    const start = time.value.split("-")[0].trim();
    const appointmentTime = `${date.value}T${start}:00`;
    if (new Date(appointmentTime) <= new Date()) { error.textContent = "Choose a future appointment."; return; }
    submit.disabled = true;
    const result = await bookAppointment({doctor: {id: doctor.id}, patient: {id: patient.id}, appointmentTime, status: 0}, localStorage.getItem("token"));
    submit.disabled = false;
    if (result.success) { dialog.close(); const status = document.getElementById("pageStatus"); if (status) status.textContent = "Appointment booked successfully."; }
    else error.textContent = result.message;
  };
}

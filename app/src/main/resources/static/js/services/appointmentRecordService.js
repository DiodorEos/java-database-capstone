import { request, mutation, segment } from "./http.js";
export const getAllAppointments = (date, name, token) => request(`/appointments/${segment(date)}/${segment(name)}/${segment(token)}`);
export const bookAppointment = (appointment, token) => mutation(`/appointments/${segment(token)}`, "POST", appointment);
export const updateAppointment = (appointment, token) => mutation(`/appointments/${segment(token)}`, "PUT", appointment);

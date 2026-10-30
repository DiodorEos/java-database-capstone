import { request, mutation, segment } from "./http.js";
export const savePrescription = (prescription, token) => mutation(`/prescription/${segment(token)}`, "POST", prescription);
export const getPrescription = (appointmentId, token) => request(`/prescription/${segment(appointmentId)}/${segment(token)}`);

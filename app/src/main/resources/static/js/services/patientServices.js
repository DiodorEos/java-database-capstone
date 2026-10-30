import { API_BASE_URL } from "../config/config.js";
import { request, mutation, segment } from "./http.js";
export const patientSignup = data => mutation("/patient", "POST", data);
// Raw Response retained for compatibility with the lab's login handler.
export async function patientLogin(data) {
  try { return await fetch(API_BASE_URL + "/patient/login", {method: "POST", headers: {"Content-Type": "application/json"}, body: JSON.stringify(data), cache: "no-store", referrerPolicy: "no-referrer"}); }
  catch { throw new Error("Unable to reach the clinic. Please try again."); }
}
export async function getPatientData(token) { return (await request(`/patient/${segment(token)}`)).patient; }
export async function getPatientAppointments(id, token, user) {
  return (await request(`/patient/${segment(id)}/${segment(user)}/${segment(token)}`)).appointments;
}
export const filterAppointments = (condition, name, token) => request(`/patient/filter/${segment(condition)}/${segment(name)}/${segment(token)}`);

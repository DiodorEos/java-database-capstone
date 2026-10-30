import { request, mutation, segment } from "./http.js";
export async function getDoctors() {
  const data = await request("/doctor");
  if (!Array.isArray(data.doctors)) throw new Error("Invalid doctor directory response.");
  return data.doctors;
}
export async function filterDoctors(name, time, specialty) {
  const data = await request(`/doctor/filter/${segment(name)}/${segment(time)}/${segment(specialty)}`);
  if (!Array.isArray(data.doctors)) throw new Error("Invalid doctor directory response.");
  return data;
}
export const deleteDoctor = (id, token) => mutation(`/doctor/${segment(id)}/${segment(token)}`, "DELETE");
export const saveDoctor = (doctor, token) => mutation(`/doctor/${segment(token)}`, "POST", doctor);

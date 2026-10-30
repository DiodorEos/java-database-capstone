import { API_BASE_URL } from "../config/config.js";
export const segment = value => encodeURIComponent(value == null || value === "" ? "null" : String(value));
export class ApiError extends Error {
  constructor(message, status = 0) { super(message); this.status = status; }
}
export async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(API_BASE_URL + path, {
      cache: "no-store", referrerPolicy: "no-referrer",
      ...options, headers: { ...(options.body ? {"Content-Type": "application/json"} : {}), ...options.headers }
    });
  } catch { throw new ApiError("Unable to reach the clinic. Please try again."); }
  let data = {};
  const text = await response.text();
  if (text) {
    try { data = JSON.parse(text); }
    catch { if (response.ok) throw new ApiError("The clinic returned an unexpected response.", response.status); }
  }
  if (!response.ok) {
    const messages = {401: "Your session is invalid or expired. Please sign in again.", 403: "You do not have access to this action.", 404: "This service is not available yet.", 409: "This action conflicts with an existing record."};
    throw new ApiError(response.status >= 500 ? "The clinic service is unavailable. Please try again." : (messages[response.status] || data.message || "The request could not be completed."), response.status);
  }
  return data;
}
export async function mutation(path, method, body) {
  try { const data = await request(path, {method, ...(body === undefined ? {} : {body: JSON.stringify(body)})}); return {success: true, message: data.message || "Saved successfully."}; }
  catch (error) { return {success: false, message: error.message}; }
}

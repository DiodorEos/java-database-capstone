# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

---

# Smart Clinic Management System - User Stories

These 17 stories cover every capability listed in the Creating User Stories lab: five Admin, six Patient, and six Doctor stories. They describe requirements, not completed features. Story points are relative effort estimates, not hours. GitHub issue links will be recorded after publication.

## Admin User Stories

### ADM-01: Sign in to the admin portal

**Title:**
_As a admin, I want to sign in with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Valid admin credentials open the admin dashboard.
2. Incorrect credentials are rejected without granting access.
3. Unauthenticated users and non-admin roles cannot access admin operations through pages or direct requests.

**Priority:** High
**Story Points:** 3
**Notes:**
- Use the admin username required by the lab; authentication must verify the role on the server.

### ADM-02: Sign out of the admin portal

**Title:**
_As a admin, I want to log out of the portal, so that I can protect system access after finishing my work._

**Acceptance Criteria:**
1. Logout clears the active login state and returns the user to the login or public page.
2. Subsequent protected requests from the logged-out browser require authentication.
3. Using browser Back does not restore usable access to admin operations.

**Priority:** High
**Story Points:** 2
**Notes:**
- Protect cached sensitive pages. Token expiry and any token revocation policy must be defined during authentication implementation.

### ADM-03: Add a doctor

**Title:**
_As a admin, I want to add doctors to the portal, so that I can make clinic doctors available for patient bookings._

**Acceptance Criteria:**
1. An authenticated admin can save a doctor with valid required profile and availability details.
2. Missing required values, invalid values, and duplicate doctor email addresses are rejected.
3. The saved doctor appears in the doctor directory; non-admin requests to add doctors are rejected.

**Priority:** High
**Story Points:** 3
**Notes:**
- Validate profile fields on the server and never expose credentials in directory responses.

### ADM-04: Delete a doctor's profile

**Title:**
_As a admin, I want to delete a doctor's profile from the portal, so that I can remove doctors who are no longer available at the clinic._

**Acceptance Criteria:**
1. An authenticated admin can confirm and delete an existing doctor profile.
2. The deleted doctor is no longer listed or available for new bookings.
3. Unauthorized requests are rejected and associated appointments are handled without orphaned records.

**Priority:** High
**Story Points:** 3
**Notes:**
- Follow the later lab's relationship/deletion requirements. Use synthetic records; retention requirements for a real clinical system require a separate assessment.

### ADM-05: Report monthly appointment counts

**Title:**
_As a admin, I want to run a stored procedure in the MySQL CLI to retrieve appointment counts per month, so that I can track clinic usage statistics._

**Acceptance Criteria:**
1. An authorized operator can invoke the procedure from the MySQL CLI using documented instructions.
2. Results identify the year and month and show counts matching the sample appointment data, without combining the same month across different years.
3. A period with no appointments is handled consistently and its output convention is documented.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Use the procedure name and parameters specified in the database lab. Application Admin status does not itself grant MySQL credentials; this is a CLI task, not a new dashboard feature.

## Patient User Stories

### PAT-01: Browse doctors without signing in

**Title:**
_As a patient, I want to view the doctor list without logging in, so that I can explore available doctors before registering._

**Acceptance Criteria:**
1. An unauthenticated visitor can open the doctor directory.
2. The list shows public doctor information such as name and specialization without passwords or private patient data.
3. An empty directory displays a clear message rather than an error.

**Priority:** High
**Story Points:** 2
**Notes:**
- Public browsing must not grant access to booking or personal records.

### PAT-02: Register a patient account

**Title:**
_As a patient, I want to sign up using my email and password, so that I can book appointments through my own account._

**Acceptance Criteria:**
1. Valid required details create a patient account.
2. Invalid input and an email already registered to a patient are rejected with a clear message.
3. Public registration cannot create an Admin or Doctor account.

**Priority:** High
**Story Points:** 3
**Notes:**
- Store passwords using an appropriate password hash; do not log passwords.

### PAT-03: Sign in to manage bookings

**Title:**
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Valid patient credentials open the patient dashboard.
2. Invalid credentials do not grant access.
3. The authenticated patient can access only their own protected booking data, including when request identifiers are changed.

**Priority:** High
**Story Points:** 3
**Notes:**
- Enforce ownership checks on the backend, not just in the interface.

### PAT-04: Sign out of the patient portal

**Title:**
_As a patient, I want to log out of the portal, so that I can secure my account when I finish using it._

**Acceptance Criteria:**
1. Logout clears the active login state and returns the user to a public or login page.
2. Subsequent requests for personal appointments from that browser require authentication.
3. Browser Back does not restore usable access to protected booking operations.

**Priority:** High
**Story Points:** 2
**Notes:**
- Protect cached personal information; define token expiry and revocation behavior during implementation.

### PAT-05: Book a one-hour appointment

**Title:**
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor at an available time._

**Acceptance Criteria:**
1. An authenticated patient can book an available future appointment lasting exactly one hour.
2. The booking is associated with that patient and the selected doctor and appears after a successful save.
3. Unauthenticated bookings, unavailable times, and overlapping bookings for the same doctor are rejected, including competing requests.

**Priority:** High
**Story Points:** 5
**Notes:**
- Check availability for the full one-hour interval and make the booking conflict check effective under concurrent requests.

### PAT-06: View upcoming appointments

**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare for my consultations._

**Acceptance Criteria:**
1. The authenticated patient can see their future active appointments with doctor, date, and time.
2. Past or cancelled appointments are not presented as upcoming active bookings; no upcoming appointments produces a clear empty state.
3. Requests cannot retrieve another patient's appointments by changing an identifier.

**Priority:** High
**Story Points:** 3
**Notes:**
- Use a consistent clinic timezone when deciding which appointments are upcoming.

## Doctor User Stories

### DOC-01: Sign in to the doctor portal

**Title:**
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Valid doctor credentials open the doctor dashboard.
2. Invalid credentials do not grant access.
3. Doctor-only operations reject unauthenticated and Patient-role requests.

**Priority:** High
**Story Points:** 3
**Notes:**
- Verify the authenticated identity and role on the backend.

### DOC-02: Sign out of the doctor portal

**Title:**
_As a doctor, I want to log out of the portal, so that I can protect my data after using the system._

**Acceptance Criteria:**
1. Logout clears the active login state and returns the user to a public or login page.
2. Subsequent protected requests from the logged-out browser require authentication.
3. Browser Back does not restore usable access to protected doctor operations.

**Priority:** High
**Story Points:** 2
**Notes:**
- Protect cached patient information and define token lifecycle behavior during implementation.

### DOC-03: View my appointment calendar

**Title:**
_As a doctor, I want to view my appointment calendar, so that I can stay organized and prepare for scheduled consultations._

**Acceptance Criteria:**
1. The authenticated doctor can view their appointments for a selected date or calendar period.
2. Entries show appointment date, time, and the patient information needed to identify the visit; an empty period is clearly indicated.
3. Changing request identifiers does not expose another doctor's appointment calendar.

**Priority:** High
**Story Points:** 3
**Notes:**
- A date-filtered schedule is sufficient unless a later lab requires a graphical calendar widget.

### DOC-04: Mark my unavailability

**Title:**
_As a doctor, I want to mark times when I am unavailable, so that I can ensure patients are offered only available slots._

**Acceptance Criteria:**
1. The authenticated doctor can mark their own time periods as unavailable.
2. Patient availability searches and booking validation exclude slots overlapping those periods.
3. Invalid periods and changes to another doctor's availability are rejected; conflicts with existing bookings are reported rather than silently cancelling them.

**Priority:** High
**Story Points:** 5
**Notes:**
- The later implementation may express unavailability by updating available slots; preserve existing booking consistency.

### DOC-05: Update my professional profile

**Title:**
_As a doctor, I want to update my specialization and contact information, so that I can give patients up-to-date information._

**Acceptance Criteria:**
1. The authenticated doctor can save valid changes to their own specialization and contact information.
2. Invalid required values are rejected without replacing the existing valid profile.
3. Saved public details appear in the doctor directory, and attempts to edit another doctor's profile are rejected.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Distinguish public professional contact details from private account information.

### DOC-06: View patient details for upcoming appointments

**Title:**
_As a doctor, I want to view patient details for my upcoming appointments, so that I can prepare for consultations._

**Acceptance Criteria:**
1. The doctor can open the relevant patient details from an upcoming appointment assigned to them.
2. The displayed patient matches the selected appointment and includes only information needed for the consultation.
3. Requests for unrelated patients or another doctor's appointment are rejected even if identifiers are changed.

**Priority:** High
**Story Points:** 3
**Notes:**
- Verify the appointment relationship server-side and avoid including credentials in responses.


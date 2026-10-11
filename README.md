# CampusRadar: Moderation + Analytics

Module 5 of CampusRadar. Users report content, moderators review it, and admins see platform statistics and manage roles.

## Features

- Any logged-in user can report a group, an event or a group message with a reason
- Moderators and admins review open reports and either **remove the content** or **dismiss** the report
- Admin dashboard with counts and three Chart.js charts (users by role, events by category, reports by status)
- Audit log of every removal, dismissal and role change
- Admins can change user roles (but not their own)

## How it works

- **Resolving a report** runs in one transaction: lock the report row with `SELECT ... FOR UPDATE`, delete the content, mark every open report on that content `REMOVED`, write an `audit_log` row, then commit. Any failure rolls back.
- **Table names in SQL:** a table name cannot be a `?` placeholder, so `ModerationDAO` picks it from a fixed `switch` over GROUP, EVENT and MESSAGE. Every user-supplied value still uses a placeholder.
- **Roles:** the `Access` helper checks the session user in each servlet (`isStaff`, `isAdmin`). A wrong role gets HTTP 403.
- **Charts:** the dashboard servlet runs `COUNT` and `GROUP BY` queries and passes arrays to Chart.js. Chart labels are stripped of unsafe characters first.
- Duplicate open reports from the same user on the same content are refused, and reports on content that does not exist are rejected.

## Files

```
src/main/java/com/campusradar/moderation/
  Access.java, AuditLog.java, Report.java
  ModerationDAO.java, AdminDAO.java
  ReportServlet.java, QueueServlet.java, ResolveServlet.java
  AdminDashboardServlet.java, UsersServlet.java
  DevLoginServlet.java        (temporary, for testing this module alone)
src/main/webapp/WEB-INF/views/moderation/
  report.jsp, queue.jsp, dashboard.jsp, users.jsp
```

`DBUtil.java` and `User.java` in the `common` package are shared with the other modules.

## Database

```sql
CREATE TABLE reports (
  id INT AUTO_INCREMENT PRIMARY KEY,
  content_type ENUM('GROUP','EVENT','MESSAGE') NOT NULL,
  content_id INT NOT NULL,
  reported_by INT NOT NULL,
  reason VARCHAR(300) NOT NULL,
  status ENUM('OPEN','REMOVED','DISMISSED') DEFAULT 'OPEN',
  handled_by INT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  handled_at TIMESTAMP NULL,
  FOREIGN KEY (reported_by) REFERENCES users(id)
);

CREATE TABLE audit_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  actor_id INT NOT NULL,
  action VARCHAR(40) NOT NULL,
  detail VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (actor_id) REFERENCES users(id)
);
```

The dashboard also reads `users`, `groups_tbl`, `events`, `event_rsvp` and `group_messages`.

## Run this module alone

```
$env:DB_URL  = "jdbc:mysql://HOST:PORT/campusradar?sslMode=REQUIRED"
$env:DB_USER = "your-db-user"
$env:DB_PASS = "your-db-password"
mvn package cargo:run
```

Open `http://localhost:8080/campusradar/mod/devlogin?id=<user id>`. A student lands on the report page, a moderator on the queue, an admin on the dashboard. Database credentials are read from environment variables and must never be committed.

## Test checklist

- [ ] A student reports a group, an event and a message
- [ ] A student opening `/mod/queue` or `/admin/dashboard` gets 403
- [ ] A moderator dismisses one report and removes another; the removed content is really gone
- [ ] A moderator opening `/admin/dashboard` gets 403
- [ ] The admin dashboard shows the counts, the three charts and the audit rows
- [ ] An admin can change another user's role but not their own

## Integration

In the combined project, `DevLoginServlet` is removed. `AuthFilter` protects `/mod/*` and `/admin/*`, and `RoleFilter` adds a second role check (with `/mod/report` open to every logged-in user).

## Team and ownership

| Role | Person | Notes |
|---|---|---|
| Original development and integration | Himanshu (`pekkaeth`) | Repository owner |
| Module lead (study, testing, documentation, extension) | Avika (`avimandliya`) | Collaborator |

**Module lead responsibilities:** understand the resolve transaction, the role checks, the audit log and the aggregate queries behind the charts; test every flow in the checklist above; keep this README accurate; build one extension.

| Contribution | Status | Commits |
|---|---|---|
| Test checklist completed and results recorded | Planned | |
| Extension: report-reason dropdown (spam, abuse, other) saved with each report | Planned | |

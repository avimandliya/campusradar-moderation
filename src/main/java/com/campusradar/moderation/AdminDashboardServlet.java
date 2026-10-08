package com.campusradar.moderation;

import com.campusradar.common.User;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final AdminDAO dao = new AdminDAO();

    // only letters, digits, space, _ and - are kept, so the text is safe inside a script block
    private String labels(Map<String, Integer> m) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String k : m.keySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(k.replaceAll("[^A-Za-z0-9 _-]", "")).append("\"");
            first = false;
        }
        return sb.append("]").toString();
    }

    private String values(Map<String, Integer> m) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Integer v : m.values()) {
            if (!first) sb.append(",");
            sb.append(v);
            first = false;
        }
        return sb.append("]").toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = Access.user(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/mod/devlogin");
            return;
        }
        if (!Access.isAdmin(user)) {
            res.sendError(403, "Only admins can open the dashboard.");
            return;
        }
        try {
            req.setAttribute("totalUsers", dao.count("SELECT COUNT(*) FROM users"));
            req.setAttribute("totalGroups", dao.count("SELECT COUNT(*) FROM groups_tbl"));
            req.setAttribute("totalEvents", dao.count("SELECT COUNT(*) FROM events"));
            req.setAttribute("totalRsvps", dao.count("SELECT COUNT(*) FROM event_rsvp"));
            req.setAttribute("totalMessages", dao.count("SELECT COUNT(*) FROM group_messages"));
            req.setAttribute("openReports", dao.count("SELECT COUNT(*) FROM reports WHERE status = 'OPEN'"));

            Map<String, Integer> roles = dao.countBy("SELECT role, COUNT(*) FROM users GROUP BY role");
            Map<String, Integer> cats = dao.countBy("SELECT category, COUNT(*) FROM events GROUP BY category");
            Map<String, Integer> reps = dao.countBy("SELECT status, COUNT(*) FROM reports GROUP BY status");
            req.setAttribute("roleLabels", labels(roles));
            req.setAttribute("roleValues", values(roles));
            req.setAttribute("catLabels", labels(cats));
            req.setAttribute("catValues", values(cats));
            req.setAttribute("repLabels", labels(reps));
            req.setAttribute("repValues", values(reps));
            req.setAttribute("audit", dao.recentAudit(10));
        } catch (Exception e) {
            req.setAttribute("error", "Could not load statistics: " + e.getMessage());
            req.setAttribute("roleLabels", "[]");
            req.setAttribute("roleValues", "[]");
            req.setAttribute("catLabels", "[]");
            req.setAttribute("catValues", "[]");
            req.setAttribute("repLabels", "[]");
            req.setAttribute("repValues", "[]");
        }
        req.getRequestDispatcher("/WEB-INF/views/moderation/dashboard.jsp").forward(req, res);
    }
}
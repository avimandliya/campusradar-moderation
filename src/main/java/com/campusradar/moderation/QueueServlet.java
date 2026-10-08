package com.campusradar.moderation;

import com.campusradar.common.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mod/queue")
public class QueueServlet extends HttpServlet {
    private final ModerationDAO dao = new ModerationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = Access.user(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/mod/devlogin");
            return;
        }
        if (!Access.isStaff(user)) {
            res.sendError(403, "Only moderators can open the review queue.");
            return;
        }
        try {
            req.setAttribute("reports", dao.listOpen());
        } catch (Exception e) {
            req.setAttribute("error", "Could not load reports: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/moderation/queue.jsp").forward(req, res);
    }
}
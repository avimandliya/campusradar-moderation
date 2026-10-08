package com.campusradar.moderation;

import com.campusradar.common.User;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mod/report")
public class ReportServlet extends HttpServlet {
    private final ModerationDAO dao = new ModerationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (Access.user(req) == null) {
            res.sendRedirect(req.getContextPath() + "/mod/devlogin");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/moderation/report.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = Access.user(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/mod/devlogin");
            return;
        }
        String type = req.getParameter("type");
        String reason = req.getParameter("reason");
        String error;
        try {
            int contentId = Integer.parseInt(req.getParameter("contentId"));
            if (type == null || !ModerationDAO.TYPES.contains(type)) {
                error = "Choose what you are reporting.";
            } else if (reason == null || reason.trim().length() < 5) {
                error = "Please explain the reason (at least 5 characters).";
            } else if (reason.trim().length() > 300) {
                error = "The reason is too long (maximum 300 characters).";
            } else {
                error = dao.createReport(type, contentId, user.getId(), reason.trim());
            }
        } catch (NumberFormatException e) {
            error = "Enter a valid ID number.";
        } catch (Exception e) {
            error = "Server error: " + e.getMessage();
        }
        if (error == null) {
            res.sendRedirect(req.getContextPath() + "/mod/report?msg="
                    + URLEncoder.encode("Thank you. Your report was sent to the moderators.", "UTF-8"));
            return;
        }
        req.setAttribute("error", error);
        req.getRequestDispatcher("/WEB-INF/views/moderation/report.jsp").forward(req, res);
    }
}
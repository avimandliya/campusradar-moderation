package com.campusradar.moderation;

import com.campusradar.common.User;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/users")
public class UsersServlet extends HttpServlet {
    private final AdminDAO dao = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = Access.user(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/mod/devlogin");
            return;
        }
        if (!Access.isAdmin(user)) {
            res.sendError(403, "Only admins can manage users.");
            return;
        }
        try {
            req.setAttribute("users", dao.listUsers());
        } catch (Exception e) {
            req.setAttribute("error", "Could not load users: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/moderation/users.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = Access.user(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/mod/devlogin");
            return;
        }
        if (!Access.isAdmin(user)) {
            res.sendError(403, "Only admins can manage users.");
            return;
        }
        String msg;
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            String role = req.getParameter("role");
            if (userId == user.getId()) {
                msg = "You cannot change your own role.";
            } else {
                msg = dao.updateRole(userId, role, user.getId());
            }
        } catch (Exception e) {
            msg = "Error: " + e.getMessage();
        }
        res.sendRedirect(req.getContextPath() + "/admin/users?msg=" + URLEncoder.encode(msg, "UTF-8"));
    }
}
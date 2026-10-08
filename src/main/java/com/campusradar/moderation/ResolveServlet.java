package com.campusradar.moderation;

import com.campusradar.common.User;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mod/resolve")
public class ResolveServlet extends HttpServlet {
    private final ModerationDAO dao = new ModerationDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = Access.user(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/mod/devlogin");
            return;
        }
        if (!Access.isStaff(user)) {
            res.sendError(403, "Only moderators can handle reports.");
            return;
        }
        String msg;
        try {
            int reportId = Integer.parseInt(req.getParameter("reportId"));
            boolean remove = "remove".equals(req.getParameter("action"));
            msg = dao.resolve(reportId, user.getId(), remove);
        } catch (Exception e) {
            msg = "Error: " + e.getMessage();
        }
        res.sendRedirect(req.getContextPath() + "/mod/queue?msg=" + URLEncoder.encode(msg, "UTF-8"));
    }
}
package com.campusradar.moderation;

import com.campusradar.common.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Access {
    public static User user(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s == null) ? null : (User) s.getAttribute("user");
    }
    public static boolean isStaff(User u) {
        return u != null && (u.getRole().equals("MODERATOR") || u.getRole().equals("ADMIN"));
    }
    public static boolean isAdmin(User u) {
        return u != null && u.getRole().equals("ADMIN");
    }
}
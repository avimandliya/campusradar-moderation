package com.campusradar.moderation;

import com.campusradar.common.DBUtil;
import com.campusradar.common.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDAO {

    public static final List<String> ROLES = Arrays.asList("STUDENT", "ORGANIZER", "MODERATOR", "ADMIN");

    // the SQL passed in here is written by us in the servlet, not typed by users
    public int count(String sql) throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public Map<String, Integer> countBy(String sql) throws Exception {
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String key = rs.getString(1);
                if (key == null || key.trim().isEmpty()) key = "Other";
                map.put(key, rs.getInt(2));
            }
        }
        return map;
    }

    public List<String[]> recentAudit(int limit) throws Exception {
        String sql = "SELECT a.created_at, u.name, a.action, a.detail FROM audit_log a "
                + "JOIN users u ON u.id = a.actor_id ORDER BY a.id DESC LIMIT " + limit;
        List<String[]> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String when = String.valueOf(rs.getTimestamp(1));
                if (when.length() >= 16) when = when.substring(0, 16);
                list.add(new String[]{ when, rs.getString(2), rs.getString(3), rs.getString(4) });
            }
        }
        return list;
    }

    public List<User> listUsers() throws Exception {
        List<User> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name, email, role FROM users ORDER BY id")) {
            while (rs.next()) {
                list.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        }
        return list;
    }

    public String updateRole(int userId, String role, int actorId) throws Exception {
        if (!ROLES.contains(role)) return "Invalid role.";
        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("UPDATE users SET role = ? WHERE id = ?")) {
                    ps.setString(1, role);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }
                AuditLog.add(con, actorId, "CHANGE_ROLE", "User #" + userId + " is now " + role);
                con.commit();
                return "Role updated.";
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }
}
package com.campusradar.moderation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLog {
    public static void add(Connection con, int actorId, String action, String detail) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO audit_log (actor_id, action, detail) VALUES (?,?,?)")) {
            ps.setInt(1, actorId);
            ps.setString(2, action);
            ps.setString(3, detail);
            ps.executeUpdate();
        }
    }
}
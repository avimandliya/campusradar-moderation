package com.campusradar.moderation;

import com.campusradar.common.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModerationDAO {

    public static final List<String> TYPES = Arrays.asList("GROUP", "EVENT", "MESSAGE");

    // table names come from this fixed list only, never from user input
    private String tableFor(String type) {
        switch (type) {
            case "GROUP":   return "groups_tbl";
            case "EVENT":   return "events";
            case "MESSAGE": return "group_messages";
            default: throw new IllegalArgumentException("Bad type");
        }
    }

    private boolean contentExists(Connection con, String type, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM " + tableFor(type) + " WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String previewOf(Connection con, String type, int id) throws SQLException {
        String col = type.equals("GROUP") ? "name" : type.equals("EVENT") ? "title" : "message";
        try (PreparedStatement ps = con.prepareStatement("SELECT " + col + " FROM " + tableFor(type) + " WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "(already removed)";
                String s = rs.getString(1);
                return (s != null && s.length() > 140) ? s.substring(0, 140) + "..." : s;
            }
        }
    }

    // returns null if the report was saved, otherwise an error message
    public String createReport(String type, int contentId, int reporterId, String reason) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            if (!contentExists(con, type, contentId)) {
                return "That " + type.toLowerCase() + " does not exist.";
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT 1 FROM reports WHERE content_type = ? AND content_id = ? AND reported_by = ? AND status = 'OPEN'")) {
                ps.setString(1, type);
                ps.setInt(2, contentId);
                ps.setInt(3, reporterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "You already reported this and it is waiting for review.";
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO reports (content_type, content_id, reported_by, reason) VALUES (?,?,?,?)")) {
                ps.setString(1, type);
                ps.setInt(2, contentId);
                ps.setInt(3, reporterId);
                ps.setString(4, reason);
                ps.executeUpdate();
            }
            return null;
        }
    }

    public List<Report> listOpen() throws Exception {
        String sql = "SELECT r.id, r.content_type, r.content_id, r.reason, r.created_at, u.name "
                + "FROM reports r JOIN users u ON u.id = r.reported_by "
                + "WHERE r.status = 'OPEN' ORDER BY r.created_at ASC, r.id ASC";
        List<Report> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Report r = new Report();
                r.setId(rs.getInt(1));
                r.setContentType(rs.getString(2));
                r.setContentId(rs.getInt(3));
                r.setReason(rs.getString(4));
                r.setCreatedAt(rs.getTimestamp(5));
                r.setReporterName(rs.getString(6));
                r.setPreview(previewOf(con, r.getContentType(), r.getContentId()));
                list.add(r);
            }
        }
        return list;
    }

    // remove = true deletes the content, false only dismisses the report. One transaction.
    public String resolve(int reportId, int moderatorId, boolean remove) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                String type;
                int contentId;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT content_type, content_id FROM reports WHERE id = ? AND status = 'OPEN' FOR UPDATE")) {
                    ps.setInt(1, reportId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { con.rollback(); return "This report was already handled."; }
                        type = rs.getString(1);
                        contentId = rs.getInt(2);
                    }
                }
                if (remove) {
                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + tableFor(type) + " WHERE id = ?")) {
                        ps.setInt(1, contentId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE reports SET status = 'REMOVED', handled_by = ?, handled_at = NOW() "
                          + "WHERE content_type = ? AND content_id = ? AND status = 'OPEN'")) {
                        ps.setInt(1, moderatorId);
                        ps.setString(2, type);
                        ps.setInt(3, contentId);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE reports SET status = 'DISMISSED', handled_by = ?, handled_at = NOW() WHERE id = ?")) {
                        ps.setInt(1, moderatorId);
                        ps.setInt(2, reportId);
                        ps.executeUpdate();
                    }
                }
                AuditLog.add(con, moderatorId, remove ? "REMOVE_CONTENT" : "DISMISS_REPORT",
                        type + " #" + contentId + " (report #" + reportId + ")");
                con.commit();
                return remove ? "Content removed." : "Report dismissed.";
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }
}
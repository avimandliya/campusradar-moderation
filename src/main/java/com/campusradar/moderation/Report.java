package com.campusradar.moderation;

import java.sql.Timestamp;

public class Report {
    private int id;
    private String contentType;
    private int contentId;
    private String reason;
    private String reporterName;
    private String preview;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public int getContentId() { return contentId; }
    public void setContentId(int contentId) { this.contentId = contentId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
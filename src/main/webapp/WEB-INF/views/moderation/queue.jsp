<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/common/header.jspf" %>
<div class="container mt-4">
  <h3>🛡️ Review queue</h3>
  <c:if test="${not empty param.msg}"><div class="alert alert-info"><c:out value="${param.msg}"/></div></c:if>
  <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

  <c:forEach items="${reports}" var="r">
    <div class="card shadow-sm mb-3">
      <div class="card-body">
        <div class="d-flex justify-content-between">
          <span class="badge bg-dark">${r.contentType} #${r.contentId}</span>
          <small class="text-muted"><fmt:formatDate value="${r.createdAt}" pattern="dd MMM, HH:mm"/></small>
        </div>
        <p class="mt-2 mb-1"><strong>Content:</strong> <c:out value="${r.preview}"/></p>
        <p class="mb-1"><strong>Reason:</strong> <c:out value="${r.reason}"/></p>
        <p class="small text-muted">Reported by <c:out value="${r.reporterName}"/></p>
        <div class="d-flex gap-2">
          <form method="post" action="${pageContext.request.contextPath}/mod/resolve"
                onsubmit="return confirm('Remove this content permanently?');">
            <input type="hidden" name="reportId" value="${r.id}">
            <input type="hidden" name="action" value="remove">
            <button class="btn btn-danger btn-sm">Remove content</button>
          </form>
          <form method="post" action="${pageContext.request.contextPath}/mod/resolve">
            <input type="hidden" name="reportId" value="${r.id}">
            <input type="hidden" name="action" value="dismiss">
            <button class="btn btn-outline-secondary btn-sm">Dismiss</button>
          </form>
        </div>
      </div>
    </div>
  </c:forEach>
  <c:if test="${empty reports}"><div class="alert alert-success">No open reports. All clear! ✅</div></c:if>
</div>
<%@ include file="/WEB-INF/common/footer.jspf" %>

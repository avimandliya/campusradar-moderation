<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/common/header.jspf" %>
<div class="container mt-4" style="max-width:560px;">
  <h3>🚩 Report content</h3>
  <p class="text-muted">See something that breaks the rules? Tell the moderators.</p>
  <c:if test="${not empty param.msg}"><div class="alert alert-success"><c:out value="${param.msg}"/></div></c:if>
  <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${pageContext.request.contextPath}/mod/report" class="card card-body shadow-sm">
    <div class="mb-3"><label class="form-label">What are you reporting?</label>
      <select name="type" class="form-select">
        <option value="GROUP" ${param.type == 'GROUP' ? 'selected' : ''}>A group</option>
        <option value="EVENT" ${param.type == 'EVENT' ? 'selected' : ''}>An event</option>
        <option value="MESSAGE" ${param.type == 'MESSAGE' ? 'selected' : ''}>A group message</option>
      </select></div>
    <div class="mb-3"><label class="form-label">ID number</label>
      <input type="number" name="contentId" class="form-control" min="1" required
             value="<c:out value='${param.contentId}'/>"></div>
    <div class="mb-3"><label class="form-label">Reason</label>
      <textarea name="reason" class="form-control" rows="3" minlength="5" maxlength="300" required><c:out value="${param.reason}"/></textarea></div>
    <button class="btn btn-danger">Send report</button>
  </form>
</div>
<%@ include file="/WEB-INF/common/footer.jspf" %>
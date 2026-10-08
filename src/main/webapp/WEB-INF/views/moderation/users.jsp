<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/common/header.jspf" %>
<div class="container mt-4">
  <h3>👥 Manage users</h3>
  <c:if test="${not empty param.msg}"><div class="alert alert-info"><c:out value="${param.msg}"/></div></c:if>
  <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
  <div class="table-responsive card shadow-sm">
    <table class="table align-middle mb-0">
      <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th></tr></thead>
      <tbody>
        <c:forEach items="${users}" var="u">
          <tr>
            <td>${u.id}</td>
            <td><c:out value="${u.name}"/></td>
            <td><c:out value="${u.email}"/></td>
            <td>
              <c:choose>
                <c:when test="${u.id == sessionScope.user.id}"><span class="badge bg-primary">${u.role} (you)</span></c:when>
                <c:otherwise>
                  <form method="post" action="${pageContext.request.contextPath}/admin/users" class="d-flex gap-2">
                    <input type="hidden" name="userId" value="${u.id}">
                    <select name="role" class="form-select form-select-sm" style="max-width:160px;">
                      <option ${u.role == 'STUDENT' ? 'selected' : ''}>STUDENT</option>
                      <option ${u.role == 'ORGANIZER' ? 'selected' : ''}>ORGANIZER</option>
                      <option ${u.role == 'MODERATOR' ? 'selected' : ''}>MODERATOR</option>
                      <option ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    </select>
                    <button class="btn btn-sm btn-outline-primary">Save</button>
                  </form>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
<%@ include file="/WEB-INF/common/footer.jspf" %>
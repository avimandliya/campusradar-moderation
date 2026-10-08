<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/common/header.jspf" %>
<div class="container mt-4">
  <h3>📊 Admin dashboard</h3>
  <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

  <div class="row g-3 mb-4">
    <div class="col-6 col-md-2"><div class="card text-center shadow-sm"><div class="card-body"><div class="fs-3">${totalUsers}</div><small>Users</small></div></div></div>
    <div class="col-6 col-md-2"><div class="card text-center shadow-sm"><div class="card-body"><div class="fs-3">${totalGroups}</div><small>Groups</small></div></div></div>
    <div class="col-6 col-md-2"><div class="card text-center shadow-sm"><div class="card-body"><div class="fs-3">${totalEvents}</div><small>Events</small></div></div></div>
    <div class="col-6 col-md-2"><div class="card text-center shadow-sm"><div class="card-body"><div class="fs-3">${totalRsvps}</div><small>RSVPs</small></div></div></div>
    <div class="col-6 col-md-2"><div class="card text-center shadow-sm"><div class="card-body"><div class="fs-3">${totalMessages}</div><small>Messages</small></div></div></div>
    <div class="col-6 col-md-2"><div class="card text-center shadow-sm border-danger"><div class="card-body"><div class="fs-3 text-danger">${openReports}</div><small>Open reports</small></div></div></div>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-md-4"><div class="card shadow-sm"><div class="card-header">Users by role</div>
      <div class="card-body"><canvas id="roleChart"></canvas></div></div></div>
    <div class="col-md-4"><div class="card shadow-sm"><div class="card-header">Events by category</div>
      <div class="card-body"><canvas id="catChart"></canvas></div></div></div>
    <div class="col-md-4"><div class="card shadow-sm"><div class="card-header">Reports by status</div>
      <div class="card-body"><canvas id="repChart"></canvas></div></div></div>
  </div>

  <div class="card shadow-sm">
    <div class="card-header">Recent moderator and admin actions</div>
    <div class="table-responsive">
      <table class="table table-sm mb-0">
        <thead><tr><th>When</th><th>Who</th><th>Action</th><th>Details</th></tr></thead>
        <tbody>
          <c:forEach items="${audit}" var="a">
            <tr><td><c:out value="${a[0]}"/></td><td><c:out value="${a[1]}"/></td>
                <td><c:out value="${a[2]}"/></td><td><c:out value="${a[3]}"/></td></tr>
          </c:forEach>
          <c:if test="${empty audit}"><tr><td colspan="4" class="text-muted">Nothing yet.</td></tr></c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script>
  new Chart(document.getElementById('roleChart'), {
    type: 'doughnut',
    data: { labels: ${roleLabels}, datasets: [{ data: ${roleValues} }] }
  });
  new Chart(document.getElementById('catChart'), {
    type: 'bar',
    data: { labels: ${catLabels}, datasets: [{ label: 'Events', data: ${catValues} }] },
    options: { scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
  });
  new Chart(document.getElementById('repChart'), {
    type: 'pie',
    data: { labels: ${repLabels}, datasets: [{ data: ${repValues} }] }
  });
</script>
<%@ include file="/WEB-INF/common/footer.jspf" %>
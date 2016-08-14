<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/tracker.jsp"); %>
<jsp:include page="${template}"/>

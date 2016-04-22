<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/login_form.jsp"); %>
<jsp:include page="${template}"/>

<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/success_message.jsp"); %>
<jsp:include page="${template}"/>


<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/thanks_message.jsp"); %>
<jsp:include page="${template}"/>

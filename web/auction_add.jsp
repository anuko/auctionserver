<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/auction_add_form.jsp"); %>
<jsp:include page="${template}"/>

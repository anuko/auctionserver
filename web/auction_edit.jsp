<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/auction_edit_form.jsp"); %>
<jsp:include page="${template}"/>

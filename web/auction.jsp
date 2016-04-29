<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/auction_view.jsp"); %>
<jsp:include page="${template}"/>

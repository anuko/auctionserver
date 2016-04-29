<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/my_bid_list.jsp"); %>
<jsp:include page="${template}"/>
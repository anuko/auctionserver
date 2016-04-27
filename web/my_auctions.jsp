<%-- Set content part name and let template handle everything. --%>
<% request.setAttribute("content_part", "/includes/my_auction_list.jsp"); %>
<jsp:include page="${template}"/>
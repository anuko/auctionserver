<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="business.User, utils.AuctionList, beans.AuctionBean, java.util.List" %>

<%
    // Obtain auction items.
    User user = (User) session.getAttribute("user");
    String userUuid = user.getUuid();
    List<AuctionBean> items = AuctionList.getUserAuctions(userUuid);
    pageContext.setAttribute("items", items);
%>

<form action="auction_add.jsp">
  <div class="login_button"><input type="submit" value="<fmt:message key="button.add_auction"/>"></div>
</form>

<c:if test="${items.size() == 0}">
    <fmt:message key="error.no_auctions"/>
</c:if>
  
<c:if test="${items.size() > 0}">
<!-- Auction list. -->
<table class="table">
    <thead>
    <tr>
        <th><fmt:message key="label.item"/></th>
        <th><fmt:message key="label.approved"/></th>
        <th><fmt:message key="label.bids"/></th>
        <th><fmt:message key="label.price"/></th>
        <th><fmt:message key="label.end"/></th>
        <th><fmt:message key="label.edit"/></th>
        <th><fmt:message key="label.delete"/></th>
    </tr>
    </thead>
    <tbody>
<c:forEach var="item" items="${pageScope.items}">
    <tr>
        <td><a href="auction_edit.jsp?uuid=${item.uuid}">${item.name}</a></td>
        <td>${item.approved}</a></td>
        <td>${item.bids}</a></td>
        <td>${item.currentPrice}</a></td>
        <td>${item.closeTimestamp}</a></td>
        <td><a href="auction_edit.jsp?uuid=${item.uuid}"><fmt:message key="label.edit"/></a></td>
        <td><a href="auction_delete.jsp?uuid=${item.uuid}"><fmt:message key="label.delete"/></a></td>
    </tr>
</c:forEach>
    </tbody>
</table>
</c:if>

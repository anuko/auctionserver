<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.User, utils.AuctionList, utils.AuctionItem, java.util.List" %>

<%
    // Obtain auction items.
    User user = (User) session.getAttribute("user");
    List<AuctionItem> items = AuctionList.getUserAuctions(user.getUuid());
    pageContext.setAttribute("items", items);
%>

<h1><fmt:message key="title.my_auctions"/></h1>
<div class="page_hint"><fmt:message key="hint.my_auctions"/></div>

<c:if test="${items.size() == 0}">
    <fmt:message key="error.no_auctions"/>
</c:if>
  
<c:if test="${items.size() > 0}">
<!-- Auction list. -->
<table class="table">
    <thead>
    <tr>
        <th><fmt:message key="label.item"/></th>
        <th><fmt:message key="label.bids"/></th>
        <th><fmt:message key="label.current_bid"/></th>
        <th><fmt:message key="label.end"/></th>
        <th><fmt:message key="label.state"/></th>
        <th><fmt:message key="label.edit"/></th>
        <th><fmt:message key="label.delete"/></th>
    </tr>
    </thead>
    <tbody>
<c:forEach var="item" items="${pageScope.items}">
    <tr>
        <td><a href="auction_edit.jsp?uuid=${item.uuid}">${item.name}</a></td>
        <td>${item.bids}</a></td>
        <td>${item.topBidString}</a></td>
        <td>${item.timeRemaining}</a></td>
        <td>${item.state}</a></td>
        <td><a href="auction_edit.jsp?uuid=${item.uuid}"><fmt:message key="label.edit"/></a></td>
        <td><a href="auction_delete.jsp?uuid=${item.uuid}"><fmt:message key="label.delete"/></a></td>
    </tr>
</c:forEach>
    </tbody>
</table>
</c:if>

<form action="auction_add.jsp">
  <div class="login_button"><input type="submit" value="<fmt:message key="button.add_auction"/>"></div>
</form>

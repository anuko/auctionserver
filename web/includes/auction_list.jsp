<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.AuctionList, utils.AuctionItem, java.util.List" %>

<%
    // Obtain auction items.
    List<AuctionItem> items = AuctionList.getAuctions();
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
        <th><fmt:message key="label.bids"/></th>
        <th><fmt:message key="label.price"/></th>
        <th><fmt:message key="label.end"/></th>
    </tr>
    </thead>
    <tbody>
<c:forEach var="item" items="${pageScope.items}">
    <tr>
        <td><a href="auction.jsp?uuid=${item.uuid}">${item.name}</a></td>
        <td>${item.bids}</a></td>
        <td>${item.topBid}</a></td>
        <td>${item.closeTimestamp}</a></td>
    </tr>
</c:forEach>
    </tbody>
</table>
</c:if>

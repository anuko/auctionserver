<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.User, utils.BidList, utils.Bid, java.util.List" %>

<%
    // Obtain auction items.
    User user = (User) session.getAttribute("user");
    List<Bid> items = BidList.getUserBids(user.getUuid());
    pageContext.setAttribute("items", items);
%>

<h1><fmt:message key="title.my_bids"/></h1>

<c:if test="${items.size() == 0}">
    <fmt:message key="error.no_bids"/>
</c:if>

<c:if test="${items.size() > 0}">
<!-- Auction list. -->
<table class="table">
    <thead>
    <tr>
        <th><fmt:message key="label.item"/></th>
        <th><fmt:message key="label.bid"/></th>
        <th><fmt:message key="label.state"/></th>
    </tr>
    </thead>
    <tbody>
<c:forEach var="item" items="${pageScope.items}">
    <tr>
        <td><a href="auction.jsp?uuid=${item.itemUuid}">${item.itemName}</a></td>
        <td>${item.bidString}</td>
        <td>${item.state}</td>
    </tr>
</c:forEach>
    </tbody>
</table>
</c:if>


<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.User, utils.BidList, beans.BidBean, java.util.List" %>

<%
    // Obtain auction items.
    User user = (User) session.getAttribute("user");
    String userUuid = user.getUuid();
    List<BidBean> items = BidList.getUserBids(userUuid);
    pageContext.setAttribute("items", items);
%>

<c:if test="${items.size() == 0}">
    <fmt:message key="error.no_bids"/>
</c:if>

<c:if test="${items.size() > 0}">
<!-- Auction list. -->
<table class="table">
    <thead>
    <tr>
        <th><fmt:message key="label.item"/></th>
        <th><fmt:message key="label.currency"/></th>
        <th><fmt:message key="label.bid"/></th>
        <th><fmt:message key="label.status"/></th>
    </tr>
    </thead>
    <tbody>
<c:forEach var="item" items="${pageScope.items}">
    <tr>
        <td><a href="auction.jsp?uuid=${item.itemUuid}">${item.itemName}</a></td>
        <td>${item.currency}</td>
        <td>${item.amount}</td>
        <td><c:if test="${item.status == null}"><fmt:message key="label.status.pending"/></c:if>${item.status}</td>
    </tr>
</c:forEach>
    </tbody>
</table>
</c:if>


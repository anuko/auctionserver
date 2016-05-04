<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.AuctionItem" %>

<%
    AuctionItem item = new AuctionItem(request.getParameter("uuid"));
    pageContext.setAttribute("item", item);
%>

<!-- Auction view. -->
<h1>${item.name}</h1>
<img class="center-block" src="${item.imageUri}" alt="${item.name}" width="300px">
<h3><fmt:message key="label.description"/></h3>
<p>${item.description}</p>

<c:if test="${item.bids > 0}">
<fmt:message key="label.top_bid"/>: ${item.currency} ${item.topBid}
</c:if>
<c:if test="${item.bids == null}">
<fmt:message key="label.currency"/>: ${item.currency}
</c:if>

<!-- Error message, if any. -->
<div class="frame-error"><p style="color:red">${sessionScope.frame_error}</p></div>

<!-- Bid form. -->
<form action="frame" method="post">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${item.uuid}">
    <div class="form-group">
      <label for="amount"><fmt:message key="label.your_bid"/>:</label>
      <input class="form-control" type="text" name="amount" value="${sessionScope.frame_amount}">
    </div>
    <div class="form-group">
      <label for="email"><fmt:message key="label.email"/>:</label>
      <input class="form-control" type="text" name="email" value="${sessionScope.frame_email}">
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="button.place_bid"/>"></div>
  </div>
</form>
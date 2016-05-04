<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.AuctionItem" %>

<%
    AuctionItem item = new AuctionItem(request.getParameter("uuid"));
    pageContext.setAttribute("item", item);
%>

<!-- Auction view. -->
<h1>${item.name}</h1>
<img class="img-responsive center-block" src="${item.imageUri}" alt="${item.name}">
<h3><fmt:message key="label.description"/></h3>
<p>${item.description}</p>

<c:if test="${item.bids > 0}">
<fmt:message key="label.top_bid"/>: ${item.currency} ${item.topBid}
</c:if>
<c:if test="${item.bids == null}">
<fmt:message key="label.currency"/>: ${item.currency}
</c:if>


<!-- Bid form. -->
<form action="bid_confirm.jsp" method="post">
  <div class="login_form">
    <input type="hidden" name="item_uuid" value="${item.uuid}">
    <div class="form-group">
      <label for="amount"><fmt:message key="label.your_bid"/>:</label>
      <input class="form-control" type="text" name="amount" value="">
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="button.place_bid"/>"></div>
  </div>
</form>

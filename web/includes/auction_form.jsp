<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="beans.AuctionBean, beans.BidBean" %>

<%
    // Create auction bean.
    AuctionBean bean = new AuctionBean(request.getParameter("uuid"));
    pageContext.setAttribute("bean", bean);
%>

<!-- Auction view. -->
<h1>${bean.name}</h1>
<img class="img-responsive center-block" src="${bean.imageUri}" alt="${bean.name}">
<h3><fmt:message key="label.description"/></h3>
<p>${bean.description}</p>

<c:if test="${bean.bids > 0}">
<fmt:message key="label.bids"/>: ${bean.bids}
</c:if>
<c:if test="${bean.bids == null}">
<fmt:message key="label.currency"/>: ${bean.currency}
</c:if>


<!-- Bid form. -->
<form action="bid_confirm.jsp" method="post">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${bean.uuid}">
    <div class="form-group">
      <label for="amount"><fmt:message key="label.your_bid"/>:</label>
      <input class="form-control" type="text" name="amount" value="">
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="button.place_bid"/>"></div>
  </div>
</form>

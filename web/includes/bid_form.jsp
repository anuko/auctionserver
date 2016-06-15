<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="utils.AuctionItem, beans.FrameBidBean, java.util.UUID" %>

<%
    // Obtain or create a bean to hold bid properties.
    FrameBidBean bidBean = (FrameBidBean) session.getAttribute("frame_bid_bean");
    if (bidBean == null) {
        AuctionItem item = new AuctionItem(request.getParameter("uuid"));
        bidBean = new FrameBidBean();
        bidBean.setUuid(UUID.randomUUID().toString());
        bidBean.setItemUuid(item.getUuid());
        bidBean.setImageUri(item.getImageUri());
        bidBean.setSellerUuid(item.getSellerUuid());
        bidBean.setItemName(item.getName());
        bidBean.setCurrency(item.getCurrency());
        bidBean.setCurrentBid(item.getTopBid());
        bidBean.setAmount(request.getParameter("amount"));
        session.setAttribute("frame_bid_bean", bidBean);
    }
    pageContext.setAttribute("bean", bidBean);
%>

<h1>${bean.itemName}</h1>
<div class="page_hint"><fmt:message key="hint.bid_put"/></div>

<!-- Error message, if any. -->
<div class="error">${sessionScope.frame_bid_error}</div>

<!-- Image. -->
<img class="center-block" src="${bean.imageUri}" alt="${bean.itemName}" width="300"></a>
<br>&nbsp;<br>

<!-- Bid confirmation form. -->
<form action="bid" method="post">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${bean.uuid}">
    <div class="form-group">
      <label for="item_currency"><fmt:message key="label.currency"/>:</label>
      <input class="form-control" type="text" name="item_currency" value="${bean.currency}" disabled>
    </div>
    <div class="form-group">
      <label for="amount"><fmt:message key="label.your_bid"/>:</label>
      <input class="form-control" type="text" name="amount" value="${bean.amount}">
    </div>
    <div class="form-group">
      <label for="amount"><fmt:message key="label.email"/>:</label>
      <input class="form-control" type="text" name="email" value="${bean.bidderEmail}">
    </div>
    <div class="login_button"><input type="submit" name="btn_confirm" value="<fmt:message key="button.place_bid"/>"></div>
  </div>
</form>

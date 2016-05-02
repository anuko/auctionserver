<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="utils.User, utils.AuctionItem, beans.BidBean, java.util.UUID" %>

<%
    // Obtain or create a bean to hold bid properties.
    BidBean bidBean = (BidBean) session.getAttribute("bid_confirm_bean");
    if (bidBean == null) {
        AuctionItem item = new AuctionItem(request.getParameter("item_uuid"));
        bidBean = new BidBean();
        bidBean.setUuid(UUID.randomUUID().toString());
        bidBean.setItemUuid(request.getParameter("item_uuid"));
        bidBean.setSellerUuid(item.getSellerUuid());
        bidBean.setItemName(item.getName());
        bidBean.setCurrency(item.getCurrency());
        bidBean.setCurrentBid(item.getTopBid());
        bidBean.setAmount(request.getParameter("amount"));
        session.setAttribute("bid_confirm_bean", bidBean);
    }
    pageContext.setAttribute("bean", bidBean);
%>

<!-- Error message, if any. -->
<div class="error">${user.errorBean.bidConfirmError}</div>

<!-- Bid confirmation form. -->
<form action="bid_confirm" method="post">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${bean.uuid}">
    <div class="form-group">
      <label for="item_name"><fmt:message key="label.item"/>:</label>
      <input class="form-control" type="text" name="item_name" value="${bean.itemName}" disabled>
    </div>
    <div class="form-group">
      <label for="item_currency"><fmt:message key="label.currency"/>:</label>
      <input class="form-control" type="text" name="item_currency" value="${bean.currency}" disabled>
    </div>
    <div class="form-group">
      <label for="amount"><fmt:message key="label.your_bid"/>:</label>
      <input class="form-control" type="text" name="amount" value="${bean.amount}">
    </div>
    <div class="login_button"><input type="submit" name="btn_confirm" value="<fmt:message key="button.confirm"/>"></div>
  </div>
</form>

<%@page import="utils.I18n"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.User, beans.AuctionBean, beans.BidBean, java.util.UUID" %>

<%
    // Obtain or create a bean to hold bid properties.
    BidBean bean = (BidBean) session.getAttribute("bid_confirm_bean");
    if (bean == null) {
        AuctionBean auctionBean = new AuctionBean(request.getParameter("uuid"));
        bean = new BidBean();
        bean.setUuid(UUID.randomUUID().toString());
        bean.setItemUuid(request.getParameter("uuid"));
        bean.setSellerUuid(auctionBean.getSellerUuid());
        bean.setItemName(auctionBean.getName());
        bean.setCurrency(auctionBean.getCurrency());
        bean.setAmount(request.getParameter("amount"));
        session.setAttribute("bid_confirm_bean", bean);
    }
    pageContext.setAttribute("bean", bean);
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

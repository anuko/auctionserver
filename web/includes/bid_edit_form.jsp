<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="utils.User, beans.BidBean " %>

<%
    // Obtain or create a bean to hold bid properties.
    BidBean bidBean = (BidBean) session.getAttribute("bid_edit_bean");
    if (bidBean == null) {
        User user = (User) session.getAttribute("user");
        String bidderUuid = user.getUuid();
        String bidUuid = request.getParameter("uuid");
        bidBean = new BidBean(bidUuid, bidderUuid);
        session.setAttribute("bid_edit_bean", bidBean);
    }
    pageContext.setAttribute("bean", bidBean);
%>

<h1><fmt:message key="title.bid_edit"/></h1>
<div class="page_hint"><fmt:message key="hint.bid_edit"/></div>

<!-- Error message, if any. -->
<div class="error">${user.errorBean.bidEditError}</div>

<!-- Bid confirmation form. -->
<form action="bid_edit" method="post">
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
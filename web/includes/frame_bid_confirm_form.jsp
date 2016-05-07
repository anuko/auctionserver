<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="beans.FrameBidConfirmBean" %>

<%
    // Obtain or create user edit bean.
    FrameBidConfirmBean bean = (FrameBidConfirmBean) session.getAttribute("frame_bid_confirm_bean");
    if (bean == null) {
        String ref = request.getParameter("ref");
        bean = new FrameBidConfirmBean(ref);
        session.setAttribute("frame_bid_confirm_bean", bean);
    }
    pageContext.setAttribute("bean", bean);
%>

<h1><fmt:message key="title.bid_confirm"/></h1>
<div class="page_hint"><fmt:message key="hint.bid_confirm"/></div>
<!-- Error message, if any. -->
<div class="error">${sessionScope.frame_bid_confirm_error}</div>

<!-- Bid confirmation form. -->
<form action="frame_bid_confirm" method="post">
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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="beans.UserConfirmBean" %>

<%@ page import="utils.AuctionItem" %>

<%
    AuctionItem item = new AuctionItem(request.getParameter("uuid"));
    pageContext.setAttribute("item", item);
%>

<h1><fmt:message key="title.pay"/></h1>
<div class="page_hint"><fmt:message key="hint.pay"/></div>

<!-- Pay form. -->
<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
  <div class="login_form">
    <input name="business" value="${item.checkoutEmail}" type="hidden">
    <input name="cmd" value="_xclick" type="hidden">
    <input name="item_number" value="${item.obfuscatedUuid}" type="hidden">
    <input name="item_name" value="${item.name}" type="hidden">
    <input name="currency_code" value="${item.currency}" type="hidden">
    <input name="amount" value="${item.topBid}" type="hidden">
    <input type="hidden" name="return" value="/thanks.jsp">
    <input type="hidden" name="rm" value="2">
    <div class="form-group">
      <label for="item_name"><fmt:message key="label.item"/>:</label>
      <input class="form-control" type="text" name="item_name" value="${item.name}" disabled>
    </div>
    <div class="form-group">
      <label for="amount"><fmt:message key="label.amount"/>:</label>
      <input class="form-control" type="text" name="amount" value="${item.topBidString}" disabled>
    </div>
    <div class="login_button"><input type="submit" name="btn_checkout_paypal" value="<fmt:message key="button.checkout_paypal"/>"></div>
  </div>
</form>


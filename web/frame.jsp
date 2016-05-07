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

<!-- Error message, if any. -->
<div class="frame-error"><p style="color:red">${sessionScope.frame_error}</p></div>

<!-- Bid form. -->
<form action="frame" method="post">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${item.uuid}">
    <table border="0">
      <tr>
        <td align="right"><fmt:message key="label.current_bid"/>:</td>
        <td><input type="text" name="current_bid" value="${item.topBidWithBidder}" disabled></td>
      </tr>
      <tr>
        <td align="right"><fmt:message key="label.time_remaining"/>:</td>
        <td><input type="text" name="end" value="${item.closeTimestamp}" disabled></td>
      </tr>
      <tr><td>&nbsp;</td></tr>
      <form action="frame" method="post">
      <tr>
        <td align="right"><fmt:message key="label.your_bid"/>:</td>
        <td><input type="text" name="amount" value="${sessionScope.frame_amount}"></td>
      </tr>
      <tr>
        <td align="right"><fmt:message key="label.email"/>:</td>
        <td><input type="text" name="email" value="${sessionScope.frame_email}"></td>
      </tr>
      <tr>
        <td colspan="2" align="center" height="50"><input type="submit" name="btn_submit" value="<fmt:message key="button.place_bid"/>"</td>
      </tr>
      </form>
    </table>

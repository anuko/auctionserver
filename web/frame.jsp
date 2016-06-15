<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.AuctionItem" %>
<link href="<c:url value="/custom.css" />" rel="stylesheet">

<%
    AuctionItem item = new AuctionItem(request.getParameter("uuid"));
    pageContext.setAttribute("item", item);
%>

<div align="center">
<!-- Auction view. -->
<h2><fmt:message key="title.auction"/></h2>
<a href="${item.itemUri}" target="_blank"><img class="center-block" src="${item.imageUri}" alt="${item.name}"></a>

<h3>${item.name}</h3>

<p>${item.description}</p>

    <table border="0">
      <tr>
        <td align="right"><fmt:message key="label.current_bid"/>:</td>
        <td align="right">${item.topBidWithBidder}</td>
      </tr>
      <tr>
        <td align="right"><fmt:message key="label.time_remaining"/>:</td>
        <td align="right">${item.timeRemaining}</td>
      </tr>
    </table>

<!-- Error message, if any. -->
<div class="frame-error"><p style="color:red">${sessionScope.frame_error}</p></div>

<!-- Bid form. -->
<form action="bid.jsp" method="post" target="_blank">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${item.uuid}">
    <table border="0">
      <form action="frame" method="post">
      <tr>
        <td align="right"><fmt:message key="label.your_bid"/>:</td>
        <td><input type="text" name="amount" value=""></td>
      </tr>
      <tr>
        <td colspan="2" align="center" height="50"><input type="submit" name="btn_submit" value="<fmt:message key="button.place_bid"/>"</td>
      </tr>
      </form>
    </table>
  </div>
<p><fmt:message key="message.other_auctions"/></p>
</div>

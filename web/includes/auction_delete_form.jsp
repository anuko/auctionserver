<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="utils.User, beans.AuctionBean" %>

<%
    // Obtain or create auction delete bean.
    AuctionBean bean = (AuctionBean) session.getAttribute("auction_delete_bean");
    if (bean == null) {
        User user = (User) session.getAttribute("user");
        String sellerUuid = user.getUuid();
        String auctionUuid = request.getParameter("uuid");
        bean = new AuctionBean(auctionUuid, sellerUuid);
        session.setAttribute("auction_delete_bean", bean);
    }
    pageContext.setAttribute("bean", bean);
%>

<h1><fmt:message key="title.auction_delete"/></h1>
<div class="page_hint"><fmt:message key="hint.auction_delete"/></div>

<!-- Error message, if any. -->
<div class="error">${user.errorBean.auctionDeleteError}</div>

<!-- Auction delete form. -->
<form action="auction_delete" method="post">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${bean.uuid}">
    
    <div class="form-group">
      <label for="name"><fmt:message key="label.name"/>:</label>
      <input class="form-control" type="text" name="name" value="${bean.name}" disabled>
    </div>
    <div class="form-group">
        <label for="duration"><fmt:message key="label.duration"/>:</label>
        <select class="form-control" name="duration" disabled>
<c:forEach var="duration" items="${applicationScope.durations}">
          <option value="${duration.days}" <c:if test="${duration.days == bean.duration}">selected</c:if>>${duration.days}</option>
</c:forEach>
        </select>
    </div>
    <div class="form-group">
        <label for="currency"><fmt:message key="label.currency"/>:</label>
        <select class="form-control" name="currency" disabled>
<c:forEach var="currency" items="${applicationScope.currencies}">
          <option value="${currency.name}" <c:if test="${currency.name == bean.currency}">selected</c:if>>${currency.name}</option>
</c:forEach>
        </select>
    </div>
    <div class="form-group">
      <label for="reserve_price"><fmt:message key="label.reserve_price"/>:</label>
      <input class="form-control" type="text" name="reserve_price" value="${bean.reservePrice}" disabled>
    </div>
    <div class="form-group">
      <label for="image_uri"><fmt:message key="label.image_uri"/>:</label>
      <input class="form-control" type="text" name="image_uri" value="${bean.imageUri}" disabled>
    </div>
    <div class="form-group">
      <label for="description"><fmt:message key="label.description"/>:</label>
      <textarea class="form-control" rows="10" name="description" disabled>${bean.description}</textarea>
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="button.delete"/>"></div>
  </div>
</form>

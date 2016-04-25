<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Auction add form. -->
<form action="auction_add" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="name"><fmt:message key="label.name"/>:</label>
      <input class="form-control" type="text" name="name" value="${sessionScope.auction_name}">
    </div>
    <div class="form-group">
        <label for="currency"><fmt:message key="label.currency"/>:</label>
        <select class="form-control" name="currency">
<c:forEach var="currency" items="${applicationScope.currencies}">
          <option value="${currency.name}" <c:if test="${currency.name == sessionScope.auction_currency}">selected</c:if>>${currency.name}</option>
</c:forEach>
        </select>
    </div>
    <div class="form-group">
      <label for="reserve_price"><fmt:message key="label.reserve_price"/>:</label>
      <input class="form-control" type="text" name="reserve_price" value="${sessionScope.auction_reserve_price}">
    </div>
    <div class="form-group">
      <label for="image_uri"><fmt:message key="label.image_uri"/>:</label>
      <input class="form-control" type="text" name="image_uri" value="${sessionScope.auction_image_uri}">
    </div>
    <div class="form-group">
      <label for="description"><fmt:message key="label.description"/>:</label>
      <textarea class="form-control" rows="10" name="description">${sessionScope.auction_description}</textarea>
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="register.button.submit"/>"></div>
  </div>
</form>

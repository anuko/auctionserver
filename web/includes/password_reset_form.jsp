<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>

<h1><fmt:message key="title.reset_password"/></h1>
<c:if test="${sessionScope.password_reset_message != null}">
    ${sessionScope.password_reset_message}
</c:if>

<c:if test="${sessionScope.password_reset_message == null}">
<div class="page_hint"><fmt:message key="hint.reset_password"/></div>
<!-- Error message, if any. -->
<div class="error">${sessionScope.password_reset_error}</div>

<!-- Login form. -->
<form action="password_reset" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="login"><fmt:message key="label.login"/>:</label>
      <input class="form-control" type="text" name="login" value="<anuko:login />">
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="button.reset_password"/>"></div>
  </div>
</form>
</c:if>
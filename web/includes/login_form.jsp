<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>

<h1><fmt:message key="title.login"/></h1>
<c:if test="${sessionScope.registration_confirmed != null}">
    ${sessionScope.registration_confirmed}
</c:if>
<c:if test="${sessionScope.registration_confirmed == null}">
<div class="page_hint"><fmt:message key="hint.login"/></div>
</c:if>

<!-- Error message, if any. -->
<div class="error">${sessionScope.login_error}</div>

<!-- Login form. -->
<form action="login" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="login"><fmt:message key="label.login"/>:</label>
      <input class="form-control" type="text" name="login" value="<anuko:login />">
    </div>
    <div class="form-group">
      <label for="password"><fmt:message key="label.password"/>:</label>
      <input class="form-control" type="password" name="password" value="${sessionScope.login_password}">
    </div>
    <div class="login_button"><input type="submit" name="btn_login" value="<fmt:message key="button.login"/>"></div>
  </div>
</form>

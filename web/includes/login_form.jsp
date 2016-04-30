<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>

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

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>

<!-- Login form. -->
<form action="login" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="login"><fmt:message key="login.label.login"/>:</label>
      <input class="form-control" type="text" name="login" value="<anuko:loginCookie />">
    </div>
    <div class="form-group">
      <label for="password"><fmt:message key="login.label.password"/>:</label>
      <input class="form-control" type="password" name="password" value="">
    </div>
    <div class="login_button"><input type="submit" name="btn_login" value="<fmt:message key="login.button.login"/>"></div>
  </div>
</form>

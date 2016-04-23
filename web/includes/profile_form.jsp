<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>

<!-- Profile form. -->
<form action="register" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="login"><fmt:message key="register.label.login"/>:</label>
      <input class="form-control" type="text" name="login" value="<anuko:loginCookie />">
    </div>
    <div class="form-group">
      <label for="password1"><fmt:message key="register.label.password"/>:</label>
      <input class="form-control" type="password" name="password1" value="${sessionScope.password1}">
    </div>
    <div class="form-group">
      <label for="password2"><fmt:message key="register.label.confirm_password"/>:</label>
      <input class="form-control" type="password" name="password2" value="${sessionScope.password2}">
    </div>
    <div class="form-group">
      <label for="full_name"><fmt:message key="register.label.full_name"/>:</label>
      <input class="form-control" type="text" name="full_name" value="${sessionScope.full_name}">
    </div>
    <div class="form-group">
      <label for="email"><fmt:message key="register.label.email"/>:</label>
      <input class="form-control" type="text" name="email" value="${sessionScope.email}">
    </div>
    <div class="login_button"><input type="submit" name="btn_save" value="<fmt:message key="button.save"/>"></div>
  </div>
</form>

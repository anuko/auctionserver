<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>

<!-- Profile form. -->
<form action="profile" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="login"><fmt:message key="register.label.login"/>:</label>
      <input class="form-control" type="text" name="login" value="${sessionScope.user_login}">
    </div>
    <div class="form-group">
      <label for="password"><fmt:message key="register.label.password"/>:</label>
      <input class="form-control" type="password" name="password" value="${sessionScope.user_password}">
    </div>
    <div class="form-group">
      <label for="confirm_password"><fmt:message key="register.label.confirm_password"/>:</label>
      <input class="form-control" type="password" name="confirm_password" value="${sessionScope.user_confirm_password}">
    </div>
    <div class="form-group">
      <label for="name"><fmt:message key="register.label.name"/>:</label>
      <input class="form-control" type="text" name="name" value="${sessionScope.user_name}">
    </div>
    <div class="form-group">
      <label for="email"><fmt:message key="register.label.email"/>:</label>
      <input class="form-control" type="text" name="email" value="${sessionScope.user_email}">
    </div>
    <div class="login_button"><input type="submit" name="btn_save" value="<fmt:message key="button.save"/>"></div>
  </div>
</form>

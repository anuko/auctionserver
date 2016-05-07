<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="beans.UserConfirmBean" %>

<%
    // Obtain or create user edit bean.
    UserConfirmBean bean = (UserConfirmBean) session.getAttribute("user_confirm_bean");
    if (bean == null) {
        String ref = request.getParameter("ref");
        bean = new UserConfirmBean(ref);
        session.setAttribute("user_confirm_bean", bean);
    }
    pageContext.setAttribute("bean", bean);
%>

<h1><fmt:message key="title.welcome"/></h1>
<div class="page_hint"><fmt:message key="hint.welcome"/></div>
<!-- Error message, if any. -->
<div class="error">${sessionScope.welcome_error}</div>

<!-- Login form. -->
<form action="user_confirm" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="login"><fmt:message key="label.login"/>:</label>
      <input class="form-control" type="text" name="login" value="${bean.login}">
    </div>
    <div class="form-group">
      <label for="name"><fmt:message key="label.name"/>:</label>
      <input class="form-control" type="text" name="name" value="${bean.name}">
    </div>
    <div class="form-group">
      <label for="password"><fmt:message key="label.password"/>:</label>
      <input class="form-control" type="password" name="password" value="${bean.password}">
    </div>
    <div class="form-group">
      <label for="confirm_password"><fmt:message key="label.confirm_password"/>:</label>
      <input class="form-control" type="password" name="confirm_password" value="${bean.confirmPassword}">
    </div>
    <div class="form-group">
      <label for="email"><fmt:message key="label.email"/>:</label>
      <input class="form-control" type="text" name="email" value="${bean.email}" disabled>
    </div>
    <div class="login_button"><input type="submit" name="btn_save" value="<fmt:message key="button.save"/>"></div>
  </div>
</form>

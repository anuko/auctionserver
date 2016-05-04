<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="beans.UserBean" %>

<%
    // Obtain or create a bean to hold form properties.
    UserBean bean = (UserBean) session.getAttribute("register_bean");
    if (bean == null) {
        bean = new UserBean();
        String email = request.getParameter("email");
        if (email != null) {
            bean.setEmail(email);
        }
        session.setAttribute("register_bean", bean);
    }
    pageContext.setAttribute("bean", bean);
%>

<h1><fmt:message key="title.register"/></h1>
<c:if test="${sessionScope.registration_successful != null}">
    ${sessionScope.registration_successful}
</c:if>

<c:if test="${sessionScope.registration_successful == null}">
<div class="page_hint"><fmt:message key="hint.register"/></div>

<!-- Error message, if any. -->
<div class="error">${sessionScope.register_error}</div>


<!-- Registration form. -->
<form action="register" method="post">
  <div class="login_form">
    <div class="form-group">
      <label for="login"><fmt:message key="label.login"/>:</label>
      <input class="form-control" type="text" name="login" value="${bean.login}">
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
      <label for="name"><fmt:message key="label.name"/>:</label>
      <input class="form-control" type="text" name="name" value="${bean.name}">
    </div>
    <div class="form-group">
      <label for="email"><fmt:message key="label.email"/>:</label>
      <input class="form-control" type="text" name="email" value="${bean.email}">
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="button.submit"/>"></div>
  </div>
</form>
</c:if>

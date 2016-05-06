<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="beans.PasswordBean" %>

<%
    // Obtain or create user edit bean.
    PasswordBean bean = (PasswordBean) session.getAttribute("password_change_bean");
    if (bean == null) {
        bean = new PasswordBean();
        String ref = request.getParameter("ref");
        if (ref != null) {
            bean.setUuid(ref);
        }
        session.setAttribute("password_change_bean", bean);
    }
    pageContext.setAttribute("bean", bean);
%>

<h1><fmt:message key="title.change_password"/></h1>
<div class="page_hint"><fmt:message key="hint.change_password"/></div>
<!-- Error message, if any. -->
<div class="error">${sessionScope.password_change_error}</div>

<!-- Login form. -->
<form action="password_change" method="post">
  <div class="login_form">
    <input type="hidden" name="uuid" value="${bean.uuid}">
    <div class="form-group">
      <label for="password"><fmt:message key="label.password"/>:</label>
      <input class="form-control" type="password" name="password" value="${bean.password}">
    </div>
    <div class="form-group">
      <label for="confirm_password"><fmt:message key="label.confirm_password"/>:</label>
      <input class="form-control" type="password" name="confirm_password" value="${bean.confirmPassword}">
    </div>
    <div class="login_button"><input type="submit" name="btn_submit" value="<fmt:message key="button.save"/>"></div>
  </div>
</form>

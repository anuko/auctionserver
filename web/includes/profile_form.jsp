<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>
<%@ page import="beans.UserBean, utils.User" %>


<%
    // Obtain or create user edit bean.
    UserBean bean = (UserBean) session.getAttribute("user_edit_bean");
    if (bean == null) {
        User user = (User) session.getAttribute("user");
        bean = new UserBean();
        bean.setUuid(user.getUuid());
        bean.setLogin(user.getLogin());
        bean.setName(user.getName());
        bean.setEmail(user.getEmail());
        session.setAttribute("user_edit_bean", bean);
    }
    pageContext.setAttribute("bean", bean);
%>

<h1><fmt:message key="title.profile"/></h1>
<div class="page_hint"><fmt:message key="hint.profile"/></div>

<!-- Error message, if any. -->
<div class="error">${user.errorBean.profileEditError}</div>

<!-- Profile form. -->
<form action="profile" method="post">
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
    <div class="login_button"><input type="submit" name="btn_save" value="<fmt:message key="button.save"/>"></div>
  </div>
</form>

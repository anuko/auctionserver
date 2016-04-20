<%--
Copyright Anuko International Ltd. (https://www.anuko.com)

LIBERAL FREEWARE LICENSE: This source code document may be used
by anyone for any purpose, and freely redistributed alone or in
combination with other software, provided that the license is obeyed.

There are only two ways to violate the license:

1. To redistribute this code in source form, with the copyright notice or
   license removed or altered. (Distributing in compiled forms without
   embedded copyright notices is permitted).

2. To redistribute modified versions of this code in *any* form
   that bears insufficient indications that the modifications are
   not the work of the original author(s).

This license applies to this document only, not any other software that it
may be combined with.
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="anuko" uri="/WEB-INF/anuko.tld" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<fmt:setLocale value="nl" />
<fmt:setBundle basename="i18n.auctionserver" />
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="auctionserver.css" rel="stylesheet" type="text/css">
  <title><fmt:message key="title"/></title>
</head>
<body>
    
<div class="mobile_layout">
<img width="300px" height="30px" src="auctionserver_logo.png">
<div class="error">${sessionScope.error}</div>
<div class="horz_centered"><a href="register.jsp"><fmt:message key="login.label.register"/></a></div>

<form action="login" method="post">
<div class="login_form">
<fmt:message key="login.label.login"/>:
<input type="text" name="login" id="login" size="25" style="width: 220px;" maxlength="100" value="<anuko:loginCookie />">
<fmt:message key="login.label.password"/>:
<input type="password" name="password" id="password" size="25" style="width: 220px;" maxlength="50" value="">
<div class="login_button"><input type="submit" name="btn_login" id="btn_login" value="<fmt:message key="login.button.login"/>"></div>
</div>
</form>
</div>

</body>
</html>
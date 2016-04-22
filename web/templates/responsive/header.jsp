<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <link href="${style}" rel="stylesheet" type="text/css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <title><fmt:message key="title"/></title>
</head>
<body>

<!-- Collapsible navigation bar -->
<nav class="navbar navbar-inverse">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="${ctx}"><fmt:message key="title"/></a>
    </div>
    <div class="collapse navbar-collapse" id="myNavbar">
      <ul class="nav navbar-nav">
        <li><a href="${ctx}/login.jsp"><fmt:message key="menu.login"/></a></li>
        <li><a href="${ctx}/register.jsp"><fmt:message key="menu.register"/></a></li>
        <li><a href="${ctx}/auctions.jsp"><fmt:message key="menu.auctions"/></a></li> 
        <li><a href="${ctx}/profile.jsp"><fmt:message key="menu.profile"/></a></li>     
      </ul>
    </div>
  </div>
</nav>

<!-- Error message, if any. -->
<div class="container">
  <div class="error">${sessionScope.error}</div>

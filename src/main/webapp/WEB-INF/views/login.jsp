<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title><spring:message code="login.title"/></title>
</head>
<body>

<jsp:include page="navbar.jsp"/>

<div class="container">
    <form action="<c:url value="/login"/>" method="post" class="form-signin">
        <c:if test="${not empty param.error}">
            <p>
                <spring:message code="login.error"/>
            </p>
        </c:if>
        <c:if test="${not empty param.logout}">
            <p>
                <spring:message code="login.logout"/>
            </p>
        </c:if>
        <h2 class="form-signin-heading"><spring:message code="login.pleaseLogin"/></h2>
        <label for="username" class="sr-only"><spring:message code="login.username"/></label>
        <input type="text" id="username" name="username" class="form-control"
               placeholder="<spring:message code="login.username"/>" required autofocus>
        <label for="password" class="sr-only"><spring:message code="login.password"/></label>
        <input type="password" id="password" name="password" class="form-control"
               placeholder="<spring:message code="login.password"/>" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit">
            <spring:message code="login.login"/>
        </button>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
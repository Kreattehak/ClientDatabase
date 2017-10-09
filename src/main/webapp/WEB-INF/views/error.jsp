<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title><spring:message code="errorPage.title"/></title>
</head>
<body>

<div class="container">
    <div class="row">
        <div class="jumbotron col-lg-10 col-lg-offset-1">
            <h1>BAD REQUEST - ${httpStatus}</h1>
        </div>
        <div class="col-lg-10 col-lg-offset-1 alert alert-danger" role="alert">
            <strong>ERROR:</strong>
            ${errorMessage}
            <p><strong>Please do not send handmade requests!</strong></p>
            <p><a href="<c:url value="/clientsTable"/>" class="alert-link">Take me back.</a></p>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>

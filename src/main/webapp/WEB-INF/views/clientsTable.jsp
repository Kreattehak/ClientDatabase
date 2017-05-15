<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title><spring:message code="clientsTable.title"/></title>
</head>
<body>

<jsp:include page="navbar.jsp"/>

<div class="container">
    <table class="table" id="clientsTable">
        <thead>
        <tr>
            <th>#</th>
            <th><spring:message code="clientsTable.firstName"/></th>
            <th><spring:message code="clientsTable.lastName"/></th>
            <th><spring:message code="clientsTable.dateOfRegistration"/></th>
            <th><spring:message code="clientsTable.cityName"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${clients}" var="client">
            <tr>
                <th scope="row">${client.id}</th>
                <td>${client.firstName}</td>
                <td>${client.lastName}</td>
                <td><fmt:formatDate value="${client.dateOfRegistration}" pattern="dd.MM.yyyy"/></td>
                <td>${client.mainAddress.cityName}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div class="form-group">
    <div class="col-lg-offset-2 col-lg-10">
        <button type="submit" id="bAddAddress" class="btn btn-info">
            <spring:message code="button.addAddress"/></button>
        <button type="submit" id="bEditClient" class="btn btn-info">
            <spring:message code="button.editClient"/></button>
        <button type="submit" id="bEditAddresses" class="btn btn-info">
            <spring:message code="button.editAddresses"/></button>
        <button type="submit" id="bEditMainAddress" class="btn btn-info">
            <spring:message code="button.editMainAddress"/></button>
        <button type="submit" id="bRemove" class="btn btn-warning">
            <spring:message code="button.editRemove"/></button>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="<c:url value="/resources/script.js"/>"></script>
</body>
</html>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title><spring:message code="addForm.client.title"></spring:message></title>
</head>
<body>

<jsp:include page="navbar.jsp"/>

<section class="container">
    <form:form modelAttribute="newClient" class="form-horizontal" method="POST">
        <form:errors path="*" cssClass="alert alert-danger" element="div"/>
        <fieldset>
            <legend><spring:message code="addForm.client.legendMessage"/></legend>
            <div class="form-group">
                <label class="control-label col-lg-2" for="firstName">
                    <spring:message code="addForm.client.firstName"/>
                </label>
                <div class="col-lg-5">
                    <form:input id="firstName" path="firstName" type="text" cssClass="form-control"/>
                    <form:errors path="firstName" cssClass="text-danger"/>
                </div>
            </div>

            <div class="form-group">
                <label class="control-label col-lg-2" for="lastName">
                    <spring:message code="addForm.client.lastName"/>
                </label>
                <div class="col-lg-5">
                    <form:input id="lastName" path="lastName" type="text" cssClass="form-control"/>
                    <form:errors path="lastName" cssClass="text-danger"/>
                </div>
            </div>

            <div class="form-group">
                <div class="col-lg-offset-2 col-lg-5">
                    <input id="addAddress" type="checkbox" name="shouldAddAddress" value="true">
                    <spring:message code="addForm.client.newAddressCheck"/>
                </div>
            </div>

            <div class="form-group">
                <div class="col-lg-offset-2 col-lg-10">
                    <input type="submit" id="btnAdd" class="btn btn-primary" value="Dodaj"/>
                </div>
            </div>
        </fieldset>
    </form:form>
</section>
<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>

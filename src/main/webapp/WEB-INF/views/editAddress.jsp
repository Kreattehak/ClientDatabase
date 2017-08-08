<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title><spring:message code="editAddress.title"/></title>
</head>
<body>

<jsp:include page="navbar.jsp"/>

<section class="container">
    <form:form modelAttribute="addressToBeEdited" class="form-horizontal" method="POST" action="/admin/editAddress">
        <form:errors path="*" cssClass="alert alert-danger" element="div"/>
        <fieldset>
            <legend><spring:message code="editAddress.legendMessage"/> ${addressToBeEdited.id}</legend>
            <div class="form-group">
                <label class="control-label col-lg-2" for="id">
                    <spring:message code="addForm.address.id"/>
                </label>
                <div class="col-lg-5">
                    <form:input id="id" path="id" type="text" cssClass="form-control" readonly="true"/>
                    <form:errors path="id" cssClass="text-danger"/>
                </div>
            </div>

            <div class="form-group">
                <label class="control-label col-lg-2" for="streetName">
                    <spring:message code="addForm.client.streetName"/>
                </label>
                <div class="col-lg-5">
                    <form:input id="streetName" path="streetName" type="text" cssClass="form-control"/>
                    <form:errors path="streetName" cssClass="text-danger"/>
                </div>
            </div>

            <div class="form-group">
                <label class="control-label col-lg-2" for="cityName">
                    <spring:message code="addForm.client.cityName"/>
                </label>
                <div class="col-lg-5">
                    <form:input id="cityName" path="cityName" type="text" cssClass="form-control"/>
                    <form:errors path="cityName" cssClass="text-danger"/>
                </div>
            </div>

            <div class="form-group">
                <label class="control-label col-lg-2" for="zipCode">
                    <spring:message code="addForm.client.zipCode"/>
                </label>
                <div class="col-lg-5">
                    <form:input id="zipCode" path="zipCode" type="text" cssClass="form-control"/>
                    <form:errors path="zipCode" cssClass="text-danger"/>
                </div>
            </div>

            <div class="form-group">
                <div class="col-lg-offset-2 col-lg-10">
                    <input type="submit" id="btnAdd" class="btn btn-primary" value="Edit"/>
                </div>
            </div>
        </fieldset>
    </form:form>
</section>
<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>

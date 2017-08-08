<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title><spring:message code="editAddresses.title"/></title>
</head>
<body>

<jsp:include page="navbar.jsp"/>

<section class="container">
    <form:form modelAttribute="usersAddresses" class="form-horizontal" method="GET" action="/admin/editAddress">
        <form:errors path="*" cssClass="alert alert-danger" element="div"/>
        <fieldset>
            <legend><spring:message code="editAddress.legendMessage"/></legend>
            <div class="form-group col-lg-6">
                <label for="userMainAddress"><spring:message code="editAddress.selectAddress"/></label>
                <select name="addressId" class="form-control" id="userMainAddress">
                    <c:forEach items="${usersAddresses}" var="address">
                        <option value="${address.key}">${address.value}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <div class="col-lg-offset-2 col-lg-10">
                    <input type="submit" id="btnEdit" class="btn btn-primary" value="Edytuj"/>
                </div>
            </div>
        </fieldset>
    </form:form>
</section>
<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>

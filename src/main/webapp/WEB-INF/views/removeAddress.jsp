<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE>
<html>
<head>
    <link rel="stylesheet"
          href="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title><spring:message code="removeAddress.title"/></title>
</head>
<body>

<jsp:include page="navbar.jsp"/>

<c:choose>
    <c:when test="${empty clientAddresses}">
        <section class="container">
            <div class="alert alert-info">
                <strong>Info:</strong> This client only have one address which is main address.
                You can't delete main address
            </div>
        </section>
    </c:when>
    <c:otherwise>
        <section class="container">
            <form:form modelAttribute="clientAddresses" class="form-horizontal" method="GET"
                       action="/admin/removeAddress">
                <form:errors path="*" cssClass="alert alert-danger" element="div"/>
                <fieldset>
                    <legend><spring:message code="removeAddress.legendMessage"/></legend>
                    <div class="form-group col-lg-5">
                        <label for="clientMainAddress"><spring:message code="editAddress.selectAddress"/></label>
                        <select name="addressId" class="form-control" id="clientMainAddress">
                            <c:forEach items="${clientAddresses}" var="address">
                                <option value="${address.key}">${address.value}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group col-lg-12">
                        <input type="submit" id="btnRemove" class="btn btn-danger" value="Usuń"/>
                    </div>
                </fieldset>
            </form:form>
            <div class="alert alert-info">
                <strong>Tip:</strong> Before you remove client's main address you need to change it to another address.
                Main address is not visible.
            </div>
        </section>
    </c:otherwise>
</c:choose>

<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>

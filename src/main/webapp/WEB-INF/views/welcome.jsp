<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="<c:url value="/resources/style.css"/>">
    <title>Welcome</title>
</head>
<body>

<jsp:include page="navbar.jsp"/>

<div class="container">
    <section>
        <table class="table" id="clientsTable">
            <thead>
            <tr>
                <th>#</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Designation</th>
                <th>Salary</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${clients}" var="client">
                <tr>
                    <th scope="row">${client.id}</th>
                    <td>${client.firstName}</td>
                    <td>${client.lastName}</td>
                    <td>${client.designation}</td>
                    <td>${client.salary}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>
</div>
<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="<c:url value="/resources/script.js"/>"></script>
</body>
</html>
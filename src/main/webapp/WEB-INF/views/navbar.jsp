<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-nav-hide"
                    aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value="/"/>"><span class="glyphicon glyphicon-picture"
                                                                    aria-hidden="true"></span> ElectroShop</a>
        </div>
        <div class="collapse navbar-collapse" id="bs-nav-hide">
            <ul class="nav navbar-nav">
                <li><a href="<c:url value="/aboutUs"/>"><spring:message code="menu.aboutUs"/></a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="<c:url value="?language=en"/>">ENG</a></li>
                <li><a href="<c:url value="?language=pl"/>">P<span class="language-polish">L</span></a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                       aria-expanded="false">
                        <spring:message code="menu.adminPanel"/><span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="<c:url value="/admin/addClient"/>"><spring:message code="menu.addClient"/></a>
                        </li>
                        <li role="separator" class="divider"></li>
                        <li>
                            <form action="<c:url value="/logout"/>" id="logout" method="post">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            </form>
                            <a href="#" onclick="document.getElementById('logout').submit();">
                                <spring:message code="menu.logOut"/></a>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

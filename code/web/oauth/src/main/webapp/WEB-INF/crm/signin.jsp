<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<html>
<head>
    <title>
        Sign In
    </title>
    <style type="text/css">
    .error { color: #FF0000; }
    .success { color: #00FF00; }

        body { font-size: larger }
    </style>
</head>
<body>
<h1> Sign In</h1>


<div>${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
</div>
<authz:authorize ifAllGranted="ROLE_USER">

    <A href="${pageContext.request.contextPath}/signout">Sign Out</A>

</authz:authorize>

<authz:authorize ifNotGranted="ROLE_USER">

    <p>
        Please enter your username and password to log into the application.
    </p>
    <c:if test="${param.error != null}">
        <p class="error">Invalid username / password</p>
    </c:if>
    <c:if test="${param.logout != null}">
        <p class="success">You have been logged out</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/crm/signin.html">
        <DIV>
            <label style="width: 100px; display: inline-block;" class="control-label" for="username"> User
                Name: </label> <br/>
            <input id="username"  name="username" type="text"/>

        </DIV>

        <DIV>
            <label style="width: 100px; display: inline-block" class="control-label" for="password"> Password: </label>
            <br/>
            <input class="input-xlarge" id="password" name="password" type="password"/>

        </DIV>
        <input type="submit"/>

         <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <p style="background-color: ActiveBorder; padding: 10px;">The preloaded (demonstration!) usernames and passwords are
        in the file <code>/services/src/main/resources/crm-schema-*.sql</code>, where
        <code>*</code> is either <code>postgresql</code> or <code>h2</code>.
        You might consult those if this is your first time signing in.
    </p>

</authz:authorize>

</body>

</html>
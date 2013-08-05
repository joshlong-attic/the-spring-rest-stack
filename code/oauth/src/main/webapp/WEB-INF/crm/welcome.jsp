<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
    <title>
        Greetings!
    </title>
</head>
<body>


<sec:authorize ifAllGranted="ROLE_USER">
    <h1>Hi, <sec:authentication property="principal.username"/>!</h1>

    <a href="${pageContext.request.contextPath}/signout">Sign Out</a>.
</sec:authorize>

<sec:authorize ifNotGranted="ROLE_USER">
    <h1>Hello, stranger.</h1>
</sec:authorize>

</body>
</html>




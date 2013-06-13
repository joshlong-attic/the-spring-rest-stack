<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>
         Hello
    </title>
</head>
<body>
<h1> Hello
</h1>


<authz:authorize ifAllGranted="ROLE_USER">
 Hi!
</authz:authorize>
</body>
</html>




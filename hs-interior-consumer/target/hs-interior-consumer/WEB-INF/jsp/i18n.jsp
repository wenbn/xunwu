<%--
  Created by IntelliJ IDEA.
  User: QD
  Date: 2017/12/14
  Time: 10:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    Language: <a href="?lang=zh_CN"><spring:message code="language.cn" /></a> - <a href="?lang=en_US"><spring:message code="language.en" /></a>
    <h2>
    <spring:message code="welcome" />
    </h2>
    Locale: ${pageContext.response.locale }
</body>
</html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    const i18n = [];
    <c:set var="classType">${param.get("classType")}</c:set>
    i18n["addTitle"] = '<spring:message code="${classType}.add"/>';
    i18n["editTitle"] = '<spring:message code="${classType}.edit"/>';

    <c:forEach var="key" items='<%=new String[]{"common.deleted","common.saved","common.enabled","common.disabled","common.errorStatus","common.confirm"}%>'>
    i18n["${key}"] = "<spring:message code="${key}"/>";
    </c:forEach>
</script>

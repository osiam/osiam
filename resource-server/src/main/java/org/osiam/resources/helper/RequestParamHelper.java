package org.osiam.resources.helper;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osiam.resources.scim.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.05.13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class RequestParamHelper {

    public Map<String, Object> getRequestParameterValues(HttpServletRequest request) {

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("filter", request.getParameter("filter"));
        parameterMap
                .put("sortBy", request.getParameter("sortBy") != null ? request.getParameter("sortBy") : "id");
        parameterMap.put("sortOrder",
                request.getParameter("sortOrder") != null ? request.getParameter("sortOrder") : "ascending");
        parameterMap.put("startIndex",
                request.getParameter("startIndex") != null ? Integer.parseInt(request.getParameter("startIndex")) : 1);
        translateAttributesForJackson(request, parameterMap);

        validateCount(request, parameterMap);

        return parameterMap;
    }

    private void translateAttributesForJackson(HttpServletRequest request, Map<String, Object> parameterMap) {
        String[] strings =
                request.getParameter("attributes") != null ? request.getParameter("attributes").split("[,|\\.]") :
                        new String[0];
        parameterMap.put("attributes", strings);
    }

    private void validateCount(HttpServletRequest request, Map<String, Object> parameterMap) {
        int count = Constants.MAX_RESULT;
        if (request.getParameter("count") != null) { count = Integer.parseInt(request.getParameter("count")); }
        if (count <= 0) { throw new IllegalArgumentException("Negative count values are not allowed"); }
        parameterMap.put("count", count);
    }
}

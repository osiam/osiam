/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
        parameterMap.put("sortBy", request.getParameter("sortBy"));
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

package org.osiam.resources.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This Controller is used to manage Root URI actions
 * <p/>
 * http://tools.ietf.org/html/draft-ietf-scim-core-schema-00
 * <p/>
 * it is based on the SCIM 2.0 API Specification:
 * <p/>
 * http://tools.ietf.org/html/draft-ietf-scim-api-00
 *
 *
 */
@Controller
@RequestMapping(value = "/")
public class RootController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String searchWithGet() {
        throw new UnsupportedOperationException("We do not support search on the root endpoint. If you have an use case " +
                "that requires this search implementation please file a bug report with our bugtracker " +
                "(see https://github.com/osiam/osiam/blob/master/README.md for details on the bug tracker).");
    }

    @RequestMapping(value = ".search", method = RequestMethod.POST)
    @ResponseBody
    public String searchWithPost() {
        throw  new UnsupportedOperationException("We do not support search on the root endpoint. If you have an use case " +
                "that requires this search implementation please file a bug report with our bugtracker " +
                "(see https://github.com/osiam/osiam/blob/master/README.md for details on the bug tracker).");
    }


}
package org.osiam;

import javax.servlet.Filter;

import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { ResourceServer.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected Filter[] getServletFilters() {
        DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");
        DelegatingFilterProxy sslRequestLoggingFilter = new DelegatingFilterProxy("sslRequestLoggingFilter");
        DelegatingFilterProxy characterEncodingFilter = new DelegatingFilterProxy("characterEncodingFilter");

        return new Filter[] { springSecurityFilterChain, sslRequestLoggingFilter, characterEncodingFilter };
    }
}

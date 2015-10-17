package org.osiam.util

import javax.servlet.ServletContext
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionContext

class TestHttpSession implements HttpSession {

    Map<String, Long> attributes = new HashMap<>()

    @Override
    long getCreationTime() {
        return System.currentTimeMillis()
    }

    @Override
    String getId() {
        return "test-session"
    }

    @Override
    long getLastAccessedTime() {
        return System.currentTimeMillis()
    }

    @Override
    ServletContext getServletContext() {
        return null
    }

    @Override
    void setMaxInactiveInterval(int interval) {

    }

    @Override
    int getMaxInactiveInterval() {
        return 0
    }

    @Override
    HttpSessionContext getSessionContext() {
        return null
    }

    @Override
    Object getAttribute(String name) {
        return attributes.get(name)
    }

    @Override
    Object getValue(String name) {
        return null
    }

    @Override
    Enumeration<String> getAttributeNames() {
        return null
    }

    @Override
    String[] getValueNames() {
        return new String[0]
    }

    @Override
    void setAttribute(String name, Object value) {
        attributes.put(name, value)
    }

    @Override
    void putValue(String name, Object value) {
        attributes.put(name, value)
    }

    @Override
    void removeAttribute(String name) {
        attributes.remove(name)
    }

    @Override
    void removeValue(String name) {
        attributes.remove(name)
    }

    @Override
    void invalidate() {
        attributes.clear()
    }

    @Override
    boolean isNew() {
        return false
    }
}

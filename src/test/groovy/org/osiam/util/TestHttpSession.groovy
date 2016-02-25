/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

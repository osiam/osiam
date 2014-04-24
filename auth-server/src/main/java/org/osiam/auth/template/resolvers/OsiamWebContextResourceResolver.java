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

package org.osiam.auth.template.resolvers;

import java.io.InputStream;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;

/**
 * Osiam web context resource resolver for thymeleaf template engine to receive input stream of requested template
 * 
 */
public class OsiamWebContextResourceResolver implements IResourceResolver {

    public static final String NAME = "SERVLETCONTEXT";

    public OsiamWebContextResourceResolver() {
        super();
    }

    public String getName() {
        return NAME;
    }

    public InputStream getResourceAsStream(final TemplateProcessingParameters templateProcessingParameters,
            final String resourceName) {

        Validate.notNull(templateProcessingParameters, "Template Processing Parameters cannot be null");
        Validate.notNull(resourceName, "Resource name cannot be null");

        final IContext context = templateProcessingParameters.getContext();
        if (!(context instanceof IWebContext)) {
            throw new TemplateProcessingException("Resource resolution by ServletContext with " +
                    this.getClass().getName() + " can only be performed " +
                    "when context implements " + IWebContext.class.getName() +
                    " [current context: " + context.getClass().getName() + "]");
        }

        return ClassLoaderUtils.getClassLoader(OsiamWebContextResourceResolver.class).getResourceAsStream(
                resourceName.toString());
    }
}
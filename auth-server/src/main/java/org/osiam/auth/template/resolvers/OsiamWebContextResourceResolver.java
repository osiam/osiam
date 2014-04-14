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
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.camel.arquillian;

import java.lang.annotation.Annotation;

import org.jboss.arquillian.container.test.impl.enricher.resource.OperatesOnDeploymentAwareProvider;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.as.camel.CamelConstants;
import org.jboss.as.camel.CamelContextRegistry;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceController;

/**
 * {@link OperatesOnDeploymentAwareProvider} implementation to
 * provide {@link CamelContextRegistry} injection to {@link ArquillianResource}-
 * annotated fields.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 19-May-2013
 */
public class CamelContextRegistryProvider implements ResourceProvider {

    @Inject
    @SuiteScoped
    private InstanceProducer<CamelContextRegistry> serviceProducer;

    @Inject
    private Instance<CamelContextRegistry> serviceInstance;

    @Override
    public boolean canProvide(final Class<?> type) {
        return type.isAssignableFrom(CamelContextRegistry.class);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        if (serviceInstance.get() == null) {
            ServiceContainer serviceContainer = CurrentServiceContainer.getServiceContainer();
            ServiceController<?> controller = serviceContainer.getService(CamelConstants.CAMEL_CONTEXT_REGISTRY_NAME);
            if (controller != null) {
                serviceProducer.set((CamelContextRegistry) controller.getValue());
            }
        }
        return serviceInstance.get();
    }
}

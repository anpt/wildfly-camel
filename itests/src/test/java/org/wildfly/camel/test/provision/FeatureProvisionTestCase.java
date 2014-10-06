/*
 * #%L
 * Wildfly Camel Testsuite
 * %%
 * Copyright (C) 2013 JBoss by Red Hat
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package org.wildfly.camel.test.provision;

import java.io.InputStream;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.gravia.provision.Provisioner;
import org.jboss.gravia.provision.Provisioner.ResourceHandle;
import org.jboss.gravia.resource.IdentityNamespace;
import org.jboss.gravia.resource.ManifestBuilder;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.camel.test.ProvisionerSupport;

/**
 * Test feature provisioning.
 *
 * @author thomas.diesler@jboss.com
 * @since 18-May-2013
 */
@RunWith(Arquillian.class)
public class FeatureProvisionTestCase {

    static String CAMEL_FEATURE = "camel.jms.feature";
    static String CAMEL_SYMBOLIC_NAME = "org.apache.camel.jms";

    @ArquillianResource
    Provisioner provisioner;

    @Deployment
    public static JavaArchive createdeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "resource-provisioner-tests");
        archive.addClasses(ProvisionerSupport.class);
        //archive.addAsResource("repository/" + CAMEL_FEATURE + ".xml");
        archive.setManifest(new Asset() {
            @Override
            public InputStream openStream() {
                ManifestBuilder builder = new ManifestBuilder();
                builder.addManifestHeader("Dependencies", "org.jboss.gravia");
                return builder.openStream();
            }
        });
        return archive;
    }

    @Test
    public void testFeatureProvisioning() throws Exception {
        ProvisionerSupport provisionerSupport = new ProvisionerSupport(provisioner);
        //provisionerSupport.populateRepository(CAMEL_FEATURE);
        List<ResourceHandle> reshandles = provisionerSupport.installCapabilities(IdentityNamespace.IDENTITY_NAMESPACE, CAMEL_FEATURE);
        try {
            ModuleLoader moduleLoader = ((ModuleClassLoader)getClass().getClassLoader()).getModule().getModuleLoader();
            moduleLoader.loadModule(ModuleIdentifier.create("deployment." + CAMEL_SYMBOLIC_NAME));
        } finally {
            for (ResourceHandle handle : reshandles) {
                handle.uninstall();
            }
        }
    }
}
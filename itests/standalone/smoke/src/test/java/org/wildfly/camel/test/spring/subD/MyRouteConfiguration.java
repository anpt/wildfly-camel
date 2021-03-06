/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2014 RedHat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.wildfly.camel.test.spring.subD;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.engine.ExplicitCamelContextNameStrategy;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRouteConfiguration extends SingleRouteCamelConfiguration {

    @Override
    public RouteBuilder route() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                getContext().setNameStrategy(new ExplicitCamelContextNameStrategy("javaConfigContext"));
                from("direct:start").transform(body().prepend("Hello "));
            }
        };
    }
}

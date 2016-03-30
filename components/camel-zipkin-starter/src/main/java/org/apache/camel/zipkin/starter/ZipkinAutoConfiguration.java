/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.zipkin.starter;

import org.apache.camel.CamelContext;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.zipkin.ZipkinEventNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZipkinConfigurationProperties.class)
@ConditionalOnProperty(value = "camel.zipkin.enabled", matchIfMissing = true)
public class ZipkinAutoConfiguration {

    @Bean(initMethod = "", destroyMethod = "")
    // Camel handles the lifecycle of this bean
    @ConditionalOnMissingBean(ZipkinEventNotifier.class)
    ZipkinEventNotifier zipkinEventNotifier(CamelContext camelContext,
                                            ZipkinConfigurationProperties config) {

        ZipkinEventNotifier notifier = new ZipkinEventNotifier();
        notifier.setHostName(config.getHostName());
        notifier.setPort(config.getPort());
        notifier.setRate(config.getRate());
        if (ObjectHelper.isNotEmpty(config.getServiceName())) {
            notifier.setServiceName(config.getServiceName());
        }
        if (config.getExcludePatterns() != null) {
            notifier.setExcludePatterns(config.getExcludePatterns());
        }
        if (config.getServiceMappings() != null) {
            notifier.setServiceMappings(config.getServiceMappings());
        }
        notifier.setIncludeMessageBody(config.isIncludeMessageBody());

        // register the bean into CamelContext
        camelContext.getManagementStrategy().addEventNotifier(notifier);

        return notifier;
    }

}

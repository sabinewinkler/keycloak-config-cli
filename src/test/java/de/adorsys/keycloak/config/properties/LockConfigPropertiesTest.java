/*
 * Copyright 2019-2020 adorsys GmbH & Co. KG @ https://adorsys.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package de.adorsys.keycloak.config.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

// From: https://tuhrig.de/testing-configurationproperties-in-spring-boot/
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {LockConfigPropertiesTest.TestConfiguration.class})
@TestPropertySource(properties = {
        "lock.enabled=true",
        "lock.key=other",
        "lock.wait-timeout=600d",
        "lock.timeout-reason=release",
})
public class LockConfigPropertiesTest {

    @Autowired
    private LockConfigProperties properties;

    @Test
    public void shouldPopulateConfigurationProperties() {
        assertThat(properties.isEnabled(), is(true));
        assertThat(properties.getKey(), is("other"));
        assertThat(properties.getWaitTimeout(), is(Duration.ofDays(600)));
        assertThat(properties.getTimeoutReason(), is(LockConfigProperties.TimeoutReason.release));
    }

    @EnableConfigurationProperties(LockConfigProperties.class)
    public static class TestConfiguration {
        // nothing
    }
}

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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration(proxyBeanMethods = false)
@ConstructorBinding
@ConfigurationProperties(prefix = "lock")
@Validated
public class LockConfigProperties {
    public static final String REALM_LOCK_ATTRIBUTE_PREFIX_KEY = "de.adorsys.keycloak.config.{0}.lock.{1}";

    @NotNull
    private boolean enabled = false;

    @NotNull
    private String key = "default";

    @NotNull
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration waitTimeout = Duration.ofSeconds(300);

    @NotNull
    private TimeoutReason timeoutReason = TimeoutReason.fail;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Duration getWaitTimeout() {
        return waitTimeout;
    }

    public void setWaitTimeout(Duration waitTimeout) {
        this.waitTimeout = waitTimeout;
    }

    public TimeoutReason getTimeoutReason() {
        return timeoutReason;
    }

    public void setTimeoutReason(TimeoutReason timeoutReason) {
        this.timeoutReason = timeoutReason;
    }

    public enum TimeoutReason {
        fail, release
    }
}

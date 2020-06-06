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

package de.adorsys.keycloak.config.lock;

import de.adorsys.keycloak.config.AbstractImportTest;
import de.adorsys.keycloak.config.properties.LockConfigProperties;
import de.adorsys.keycloak.config.repository.lock.LockModel;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class LockServiceIT extends AbstractImportTest {
    private static final String REALM_NAME = "lockRealm";

    LockServiceIT() {
        this.resourcePath = "import-files/lock";
    }

    @Test
    @Order(0)
    public void shouldCreateSimpleRealm() {
        setLock(Instant.now().minusSeconds(10));

        doImport("0_create_simple-realm.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();

        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));
        assertThat(createdRealm.getLoginTheme(), is(nullValue()));
        assertThat(
                createdRealm.getAttributes().get("de.adorsys.keycloak.config.import-checksum-default"),
                is("6292be0628c50ff8fc02bd4092f48a731133e4802e158e7bc2ba174524b4ccf1")
        );
    }

    private void setLock(Instant timestamp) {
        Map<String, String> customAttributes = keycloakProvider.get().realm("master")
                .toRepresentation().getAttributes();

        String customAttribute = MessageFormat.format(
                LockConfigProperties.REALM_LOCK_ATTRIBUTE_PREFIX_KEY,
                REALM_NAME,
                "default"
        );

        LockModel lockValue = new LockModel();
        lockValue.setPid("0");
        lockValue.setHostname("JUNIT");
        lockValue.setTimestamp(timestamp);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            customAttributes.put(customAttribute, objectMapper.writeValueAsString(lockValue));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

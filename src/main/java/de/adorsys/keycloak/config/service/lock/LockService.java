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

package de.adorsys.keycloak.config.service.lock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.keycloak.config.exception.ImportProcessingException;
import de.adorsys.keycloak.config.model.RealmImport;
import de.adorsys.keycloak.config.properties.LockConfigProperties;
import de.adorsys.keycloak.config.repository.RealmRepository;
import de.adorsys.keycloak.config.repository.lock.LockModel;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Map;

@Service
public class LockService {
    private static final Logger logger = LoggerFactory.getLogger(LockService.class);

    private final RealmRepository realmRepository;
    private final LockConfigProperties lockConfigProperties;

    private final ObjectMapper objectMapper;

    @Autowired
    public LockService(
            RealmRepository realmRepository,
            LockConfigProperties lockConfigProperties,
            ObjectMapper objectMapper
    ) {
        this.realmRepository = realmRepository;
        this.lockConfigProperties = lockConfigProperties;
        this.objectMapper = objectMapper;
    }

    public void getLock(RealmImport realmImport) {
        if (!lockConfigProperties.isEnabled()) return;

        LockModel lockValue;

        while ((lockValue = readLockAttribute(realmImport)) != null) {
            logger.info("Waiting for import lock ...");

            if (Instant.now().isAfter(lockValue.getTimestamp().plus(lockConfigProperties.getWaitTimeout()))) {
                handleLockTimeout(lockValue);
            }
        }

        setLock(realmImport);
    }

    public void setLock(RealmImport realmImport) {
        if (!lockConfigProperties.isEnabled()) return;

        LockModel value = new LockModel();
        try {
            value.setTimestamp(Instant.now());
            value.setHostname(InetAddress.getLocalHost().getHostName());
            value.setPid(new ApplicationPid().toString());
        } catch (Exception e) {
            throw new ImportProcessingException("Error while update lock attribute in realm", e);
        }

        updateLockAttribute(realmImport, value);

        logger.debug("Ensure lock for realm '{}'.", realmImport.getRealm());
    }

    public void releaseLock(RealmImport realmImport) {
        if (!lockConfigProperties.isEnabled()) return;

        updateLockAttribute(realmImport, null);

        logger.debug("Release lock for realm '{}'.", realmImport.getRealm());
    }

    private void handleLockTimeout(LockModel lockValue) {
        if (lockConfigProperties.getTimeoutReason() == LockConfigProperties.TimeoutReason.release) {
            logger.warn(MessageFormat.format(
                    "Release lock by {0} (PID: {1}) since {2} after {3} seconds.",
                    lockValue.getHostname(),
                    lockValue.getPid(),
                    lockValue.getTimestamp().toString(),
                    Instant.now().minusMillis(lockValue.getTimestamp().toEpochMilli()).getEpochSecond()
            ));
        } else {
            throw new ImportProcessingException(
                    MessageFormat.format(
                            "Could not acquire lock. Currently locked by {0} (PID: {1}) since {2}.",
                            lockValue.getHostname(),
                            lockValue.getPid(),
                            lockValue.getTimestamp().toString()
                    )
            );
        }
    }

    private LockModel readLockAttribute(RealmImport realmImport) {
        RealmRepresentation masterRealm = realmRepository.get("master");
        Map<String, String> customAttributes = masterRealm.getAttributes();

        String customAttribute = MessageFormat.format(
                LockConfigProperties.REALM_LOCK_ATTRIBUTE_PREFIX_KEY,
                realmImport.getRealm(),
                lockConfigProperties.getKey()
        );

        LockModel lockValue;

        String value = customAttributes.get(customAttribute);
        if (value == null) {
            return null;
        }

        try {
            lockValue = objectMapper.readValue(value, LockModel.class);
        } catch (JsonProcessingException e) {
            throw new ImportProcessingException("Error while reading lock attribute in realm master", e);
        }

        return lockValue;
    }

    private void updateLockAttribute(RealmImport realmImport, LockModel value) {
        RealmRepresentation masterRealm = realmRepository.get("master");
        Map<String, String> customAttributes = masterRealm.getAttributes();

        String customAttribute = MessageFormat.format(
                LockConfigProperties.REALM_LOCK_ATTRIBUTE_PREFIX_KEY,
                realmImport.getRealm(),
                lockConfigProperties.getKey()
        );

        if (value == null) {
            customAttributes.remove(customAttribute);
        } else {
            try {
                customAttributes.put(customAttribute, objectMapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new ImportProcessingException("Error while update lock attribute in realm master", e);
            }
        }

        realmRepository.update(masterRealm);
    }
}

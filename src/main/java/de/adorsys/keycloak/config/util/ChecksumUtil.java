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

package de.adorsys.keycloak.config.util;

import org.apache.commons.codec.digest.DigestUtils;

public class ChecksumUtil {

    public static String checksum(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Cannot calculate checksum of null");
        }

        return DigestUtils.sha256Hex(text);
    }

    public static String checksum(byte[] textInBytes) {
        if (textInBytes == null) {
            throw new IllegalArgumentException("Cannot calculate checksum of null");
        }

        return DigestUtils.sha256Hex(textInBytes);
    }
}

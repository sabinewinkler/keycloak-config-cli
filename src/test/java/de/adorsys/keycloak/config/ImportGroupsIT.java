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

package de.adorsys.keycloak.config;

import de.adorsys.keycloak.config.exception.KeycloakRepositoryException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class ImportGroupsIT extends AbstractImportTest {
    private static final String REALM_NAME = "realmWithGroups";

    ImportGroupsIT() {
        this.resourcePath = "import-files/groups";
    }

    @Test
    @Order(0)
    public void shouldCreateRealmWithGroups() {
        doImport("0_create_realm_with_group.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation createdGroup = loadGroup("/My Group");

        assertThat("name not equal", createdGroup.getName(), is("My Group"));
        assertThat("path not equal", createdGroup.getPath(), is("/My Group"));
        assertThat("attributes is null", createdGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", createdGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles not null", createdGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups not empty", createdGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(1)
    public void shouldUpdateRealmAddGroup() {
        doImport("1_update_realm_add_group.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation existingGroup = loadGroup("/My Group");

        assertThat("name not equal", existingGroup.getName(), is("My Group"));
        assertThat("path not equal", existingGroup.getPath(), is("/My Group"));
        assertThat("attributes is null", existingGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", existingGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", existingGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups is null", existingGroup.getSubGroups(), hasSize(0));

        GroupRepresentation addedGroup = loadGroup("/My Added Group");

        assertThat("name not equal", addedGroup.getName(), is("My Added Group"));
        assertThat("path not equal", addedGroup.getPath(), is("/My Added Group"));
        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", addedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups is null", addedGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(2)
    public void shouldUpdateRealmAddGroupWithAttribute() {
        doImport("2_update_realm_add_group_with_attribute.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation addedGroup = loadGroup("/Group with attribute");

        assertThat("name not equal", addedGroup.getName(), is("Group with attribute"));
        assertThat("path not equal", addedGroup.getPath(), is("/Group with attribute"));

        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes is null", addedGroup.getAttributes(), hasEntry(is("my attribute"), containsInAnyOrder("my attribute value")));

        assertThat("realm roles is null", addedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups is null", addedGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(3)
    public void shouldUpdateRealmAddGroupWithRealmRole() {
        doImport("3_update_realm_add_group_with_realm_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation addedGroup = loadGroup("/Group with realm role");

        assertThat("name not equal", addedGroup.getName(), is("Group with realm role"));
        assertThat("path not equal", addedGroup.getPath(), is("/Group with realm role"));
        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", addedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups is null", addedGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(4)
    public void shouldUpdateRealmAddGroupWithClientRole() {
        doImport("4_update_realm_add_group_with_client_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation addedGroup = loadGroup("/Group with client role");

        assertThat("name not equal", addedGroup.getName(), is("Group with client role"));
        assertThat("path not equal", addedGroup.getPath(), is("/Group with client role"));
        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", addedGroup.getRealmRoles(), hasSize(0));

        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", addedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        assertThat("subgroups is null", addedGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(5)
    public void shouldUpdateRealmAddGroupWithSubGroup() {
        doImport("5_update_realm_add_group_with_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation addedGroup = loadGroup("/Group with subgroup");

        assertThat("name not equal", addedGroup.getName(), is("Group with subgroup"));
        assertThat("path not equal", addedGroup.getPath(), is("/Group with subgroup"));
        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", addedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups is null", addedGroup.getSubGroups(), is(not(nullValue())));
        assertThat("subgroups is empty", addedGroup.getSubGroups(), is(hasSize(1)));

        GroupRepresentation subGroup = addedGroup.getSubGroups().get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/Group with subgroup/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(6)
    public void shouldUpdateRealmAddGroupWithSubGroupWithRealmRole() {
        doImport("6_update_realm_add_group_with_subgroup_with_realm_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation addedGroup = loadGroup("/Group with subgroup with realm role");

        assertThat("name not equal", addedGroup.getName(), is("Group with subgroup with realm role"));
        assertThat("path not equal", addedGroup.getPath(), is("/Group with subgroup with realm role"));
        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", addedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups is null", addedGroup.getSubGroups(), is(not(nullValue())));
        assertThat("subgroups is empty", addedGroup.getSubGroups(), is(hasSize(1)));

        GroupRepresentation subGroup = addedGroup.getSubGroups().get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/Group with subgroup with realm role/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(7)
    public void shouldUpdateRealmAddGroupWithSubGroupWithClientRole() {
        doImport("7_update_realm_add_group_with_subgroup_with_client_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation addedGroup = loadGroup("/Group with subgroup with client role");

        assertThat("name not equal", addedGroup.getName(), is("Group with subgroup with client role"));
        assertThat("path not equal", addedGroup.getPath(), is("/Group with subgroup with client role"));
        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", addedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups is null", addedGroup.getSubGroups(), is(not(nullValue())));
        assertThat("subgroups is empty", addedGroup.getSubGroups(), is(hasSize(1)));

        GroupRepresentation subGroup = addedGroup.getSubGroups().get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/Group with subgroup with client role/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));

        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));

        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(8)
    public void shouldUpdateRealmAddGroupWithSubGroupWithSubGroup() {
        doImport("8_update_realm_add_group_with_subgroup_with_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation addedGroup = loadGroup("/Group with subgroup with subgroup");

        assertThat("name not equal", addedGroup.getName(), is("Group with subgroup with subgroup"));
        assertThat("path not equal", addedGroup.getPath(), is("/Group with subgroup with subgroup"));
        assertThat("attributes is null", addedGroup.getAttributes(), aMapWithSize(0));
        assertThat("realm roles is null", addedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", addedGroup.getClientRoles(), aMapWithSize(0));

        List<GroupRepresentation> subGroups = addedGroup.getSubGroups();
        assertThat("subgroups is null", subGroups, is(not(nullValue())));
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/Group with subgroup with subgroup/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));

        List<GroupRepresentation> innerSubGroups = subGroup.getSubGroups();
        assertThat("subgroup's subgroups is null", innerSubGroups, hasSize(1));

        GroupRepresentation innerSubGroup = innerSubGroups.get(0);
        assertThat("subgroup is null", innerSubGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", innerSubGroup.getName(), is("My Inner SubGroup"));
        assertThat("subgroup's path not equal", innerSubGroup.getPath(), is("/Group with subgroup with subgroup/My SubGroup/My Inner SubGroup"));
        assertThat("subgroup's attributes is null", innerSubGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", innerSubGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", innerSubGroup.getClientRoles(), aMapWithSize(0));
    }

    @Test
    @Order(9)
    public void shouldUpdateRealmUpdateGroupAddAttribute() {
        doImport("9_update_realm_update_group_add_attribute.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups not empty", updatedGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(10)
    public void shouldUpdateRealmUpdateGroupAddRealmRole() {
        doImport("10_update_realm_update_group_add_realm_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroups not empty", updatedGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(11)
    public void shouldUpdateRealmUpdateGroupAddClientRole() {
        doImport("11_update_realm_update_group_add_client_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"))
        ;
        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));
        assertThat("subgroups not empty", updatedGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(12)
    public void shouldUpdateRealmUpdateGroupAddSubgroup() {
        doImport("12_update_realm_update_group_add_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));
        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(13)
    public void shouldUpdateRealmUpdateGroupAddSecondAttributeValue() {
        doImport("13_update_realm_update_group_add_second_attribute_value.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value", "my added attribute second value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));
        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(14)
    public void shouldUpdateRealmUpdateGroupAddSecondAttribute() {
        doImport("14_update_realm_update_group_add_second_attribute.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));
        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(2));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value", "my added attribute second value")));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my second added attribute"), containsInAnyOrder("my second added attribute value", "my second added attribute second value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(15)
    public void shouldUpdateRealmUpdateGroupChangeAttributeValue() {
        doImport("15_update_realm_update_group_change_attribute_value.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(2));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value", "my added attribute second value")));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my second added attribute"), containsInAnyOrder("my changed attribute value", "my second added attribute second value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));
        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(16)
    public void shouldUpdateRealmUpdateGroupChangeAttributeKey() {
        doImport("16_update_realm_update_group_change_attribute_key.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes is null", updatedGroup.getAttributes(), aMapWithSize(2));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my added attribute"), containsInAnyOrder("my added attribute value", "my added attribute second value")));
        assertThat("attributes is null", updatedGroup.getAttributes(), hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value", "my second added attribute second value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(17)
    public void shouldUpdateRealmUpdateGroupDeleteAttribute() {
        doImport("17_update_realm_update_group_delete_attribute.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(), hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value", "my second added attribute second value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(18)
    public void shouldUpdateRealmUpdateGroupDeleteAttributeValue() {
        doImport("18_update_realm_update_group_delete_attribute_value.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(), hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));

        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(19)
    public void shouldUpdateRealmUpdateGroupAddSecondRealmRole() {
        doImport("19_update_realm_update_group_add_scond_realm_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(), hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), containsInAnyOrder("my_realm_role", "my_second_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(20)
    public void shouldUpdateRealmUpdateGroupDeleteRealmRole() {
        doImport("20_update_realm_update_group_delete_realm_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(21)
    public void shouldUpdateRealmUpdateGroupDeleteLastRealmRole() {
        doImport("21_update_realm_update_group_delete_last_realm_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));
        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(), hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(22)
    public void shouldUpdateRealmUpdateGroupAddSecondClientRole() {
        doImport("22_update_realm_update_group_delete_add_second_client_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));

        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role", "my_second_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(23)
    public void shouldUpdateRealmUpdateGroupRemoveClientRole() {
        doImport("23_update_realm_update_group_delete_remove_client_role.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(24)
    public void shouldUpdateRealmUpdateGroupAddClientRolesFromSecondClient() {
        doImport("24_update_realm_update_group_delete_add_client_roles_from_second_client.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));

        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(2));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(25)
    public void shouldUpdateRealmUpdateGroupRemoveClient() {
        doImport("25_update_realm_update_group_delete_remove_client.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));


        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(26)
    public void shouldUpdateRealmUpdateGroupAddAttributeToSubGroup() {
        doImport("26_update_realm_update_group_add_attribute_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my subgroup attribute"), containsInAnyOrder("my subgroup attribute value")));

        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(27)
    public void shouldUpdateRealmUpdateGroupAddAttributeValueToSubGroup() {
        doImport("27.0_update_realm_update_group_add_attribute_value_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my subgroup attribute"), containsInAnyOrder("my subgroup attribute value", "my subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(28)
    public void shouldUpdateRealmUpdateGroupAddSecondAttributeToSubGroup() {
        doImport("27.1_update_realm_update_group_add_second_attribute_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));

        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(2));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my subgroup attribute"), containsInAnyOrder("my subgroup attribute value", "my subgroup attribute second value")));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));

        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(29)
    public void shouldUpdateRealmUpdateGroupRemoveAttributeValueFromSubGroup() {
        doImport("28_update_realm_update_group_remove_attribute_value_from_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));
        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(2));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my subgroup attribute"), containsInAnyOrder("my subgroup attribute second value")));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(30)
    public void shouldUpdateRealmUpdateGroupRemoveAttributeFromSubGroup() {
        doImport("29_update_realm_update_group_remove_attribute_from_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(31)
    public void shouldUpdateRealmUpdateGroupAddRealmRoleToSubGroup() {
        doImport("30_update_realm_update_group_add_realm_role_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));

        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));


        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(32)
    public void shouldUpdateRealmUpdateGroupAddSecondRealmRoleToSubGroup() {
        doImport("31_update_realm_update_group_add_second_role_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), containsInAnyOrder("my_realm_role", "my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(33)
    public void shouldUpdateRealmUpdateGroupRemoveRealmRoleFromSubGroup() {
        doImport("32_update_realm_update_group_remove_role_from_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(0));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(34)
    public void shouldUpdateRealmUpdateGroupAddClientRoleToSubGroup() {
        doImport("33_update_realm_update_group_add_client_role_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(35)
    public void shouldUpdateRealmUpdateGroupAddSecondClientRoleToSubGroup() {
        doImport("34_update_realm_update_group_add_second_client_role_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role", "my_second_client_role")));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(36)
    public void shouldUpdateRealmUpdateGroupAddSecondClientRolesToSubGroup() {
        doImport("35_update_realm_update_group_add_second_client_roles_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(2));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role", "my_second_client_role")));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(37)
    public void shouldUpdateRealmUpdateGroupRemoveClientRoleFromSubGroup() {
        doImport("36_update_realm_update_group_remove_client_role_from_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(2));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(38)
    public void shouldUpdateRealmUpdateGroupRemoveClientRolesFromSubGroup() {
        doImport("37_update_realm_update_group_remove_client_roles_from_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));
        assertThat("subgroup's subgroups is null", subGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(39)
    public void shouldUpdateRealmUpdateGroupAddSubGroupToSubGroup() {
        doImport("38_update_realm_update_group_add_subgroup_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));

        List<GroupRepresentation> innerSubGroups = subGroup.getSubGroups();
        assertThat("inner subgroups is empty", innerSubGroups, is(hasSize(1)));

        GroupRepresentation innerSubGroup = innerSubGroups.get(0);
        assertThat("inner subgroup is null", innerSubGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", innerSubGroup.getName(), is("My inner SubGroup"));
        assertThat("subgroup's path not equal", innerSubGroup.getPath(), is("/My Group/My SubGroup/My inner SubGroup"));
        assertThat("subgroup's attributes is null", innerSubGroup.getAttributes(), aMapWithSize(0));
        assertThat("subgroup's realm roles is null", innerSubGroup.getRealmRoles(), hasSize(0));
        assertThat("subgroup's client roles is null", innerSubGroup.getClientRoles(), aMapWithSize(0));
    }

    @Test
    @Order(40)
    public void shouldUpdateRealmUpdateGroupAddSecondSubGroupToSubGroup() {
        doImport("39_update_realm_update_group_add_second subgroup_to_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));


        List<GroupRepresentation> innerSubGroups = subGroup.getSubGroups();
        assertThat("inner subgroups is empty", innerSubGroups, is(hasSize(2)));

        GroupRepresentation innerSubGroup = innerSubGroups.stream()
                .filter(s -> Objects.equals(s.getName(), "My inner SubGroup"))
                .findFirst()
                .orElse(null);
        assertThat("inner subgroup is null", innerSubGroup, is(not(nullValue())));
        assertThat("inner subgroup's name not equal", innerSubGroup.getName(), is("My inner SubGroup"));
        assertThat("inner subgroup's path not equal", innerSubGroup.getPath(), is("/My Group/My SubGroup/My inner SubGroup"));
        assertThat("inner subgroup's attributes is null", innerSubGroup.getAttributes(), aMapWithSize(0));
        assertThat("inner subgroup's realm roles is null", innerSubGroup.getRealmRoles(), hasSize(0));
        assertThat("inner subgroup's client roles is null", innerSubGroup.getClientRoles(), aMapWithSize(0));
        assertThat("inner subgroup's subgroups is null", innerSubGroup.getSubGroups(), hasSize(0));

        innerSubGroup = innerSubGroups.stream()
                .filter(s -> Objects.equals(s.getName(), "My second inner SubGroup"))
                .findFirst()
                .orElse(null);
        assertThat("inner subgroup is null", innerSubGroup, is(not(nullValue())));
        assertThat("inner subgroup's name not equal", innerSubGroup.getName(), is("My second inner SubGroup"));
        assertThat("inner subgroup's path not equal", innerSubGroup.getPath(), is("/My Group/My SubGroup/My second inner SubGroup"));
        assertThat("inner subgroup's attributes is null", innerSubGroup.getAttributes(), aMapWithSize(0));
        assertThat("inner subgroup's realm roles is null", innerSubGroup.getRealmRoles(), hasSize(0));
        assertThat("inner subgroup's client roles is null", innerSubGroup.getClientRoles(), aMapWithSize(0));
        assertThat("inner subgroup's subgroups is null", innerSubGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(41)
    public void shouldUpdateRealmUpdateGroupUpdateSubGroupInSubGroup() {
        doImport("40_update_realm_update_group_update_subgroup_in_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));


        List<GroupRepresentation> innerSubGroups = subGroup.getSubGroups();
        assertThat("inner subgroups is empty", innerSubGroups, is(hasSize(2)));

        GroupRepresentation innerSubGroup = innerSubGroups.stream()
                .filter(s -> Objects.equals(s.getName(), "My inner SubGroup"))
                .findFirst()
                .orElse(null);
        assertThat("inner subgroup is null", innerSubGroup, is(not(nullValue())));
        assertThat("inner subgroup's name not equal", innerSubGroup.getName(), is("My inner SubGroup"));
        assertThat("inner subgroup's path not equal", innerSubGroup.getPath(), is("/My Group/My SubGroup/My inner SubGroup"));
        assertThat("inner subgroup's attributes is null", innerSubGroup.getAttributes(), aMapWithSize(1));
        assertThat("inner subgroup's attributes is null", innerSubGroup.getAttributes(), hasEntry(is("my inner subgroup attribute"), containsInAnyOrder("my inner subgroup attribute value", "my inner subgroup attribute second value")));
        assertThat("inner subgroup's realm roles is null", innerSubGroup.getRealmRoles(), contains("my_realm_role"));
        assertThat("inner subgroup's client roles is null", innerSubGroup.getClientRoles(), aMapWithSize(1));
        assertThat("inner subgroup's client roles is null", innerSubGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));

        List<GroupRepresentation> innerInnerSubGroups = innerSubGroup.getSubGroups();
        assertThat("inner subgroup's subgroups is null", innerInnerSubGroups, hasSize(1));
        GroupRepresentation innerInnerSubGroup = innerInnerSubGroups.get(0);
        assertThat("inner inner subgroup is null", innerInnerSubGroup, is(not(nullValue())));
        assertThat("inner inner subgroup's name not equal", innerInnerSubGroup.getName(), is("My inner inner SubGroup"));
        assertThat("inner inner subgroup's path not equal", innerInnerSubGroup.getPath(), is("/My Group/My SubGroup/My inner SubGroup/My inner inner SubGroup"));
        assertThat("inner inner subgroup's attributes is null", innerInnerSubGroup.getAttributes(), aMapWithSize(0));
        assertThat("inner inner subgroup's realm roles is null", innerInnerSubGroup.getRealmRoles(), hasSize(0));
        assertThat("inner inner subgroup's client roles is null", innerInnerSubGroup.getClientRoles(), aMapWithSize(0));


        innerSubGroup = innerSubGroups.stream()
                .filter(s -> Objects.equals(s.getName(), "My second inner SubGroup"))
                .findFirst()
                .orElse(null);
        assertThat("inner subgroup is null", innerSubGroup, is(not(nullValue())));
        assertThat("inner subgroup's name not equal", innerSubGroup.getName(), is("My second inner SubGroup"));
        assertThat("inner subgroup's path not equal", innerSubGroup.getPath(), is("/My Group/My SubGroup/My second inner SubGroup"));
        assertThat("inner subgroup's attributes is null", innerSubGroup.getAttributes(), aMapWithSize(0));
        assertThat("inner subgroup's realm roles is null", innerSubGroup.getRealmRoles(), hasSize(0));
        assertThat("inner subgroup's client roles is null", innerSubGroup.getClientRoles(), aMapWithSize(0));
        assertThat("inner subgroup's subgroups is null", innerSubGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(42)
    public void shouldUpdateRealmUpdateGroupDeleteSubGroupInSubGroup() {
        doImport("41_update_realm_update_group_delete_subgroup_in_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        List<GroupRepresentation> subGroups = updatedGroup.getSubGroups();
        assertThat("subgroups is empty", subGroups, is(hasSize(1)));

        GroupRepresentation subGroup = subGroups.get(0);
        assertThat("subgroup is null", subGroup, is(not(nullValue())));
        assertThat("subgroup's name not equal", subGroup.getName(), is("My SubGroup"));
        assertThat("subgroup's path not equal", subGroup.getPath(), is("/My Group/My SubGroup"));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), aMapWithSize(1));
        assertThat("subgroup's attributes is null", subGroup.getAttributes(), hasEntry(is("my second subgroup attribute"), containsInAnyOrder("my second subgroup attribute value", "my second subgroup attribute second value")));
        assertThat("subgroup's realm roles is null", subGroup.getRealmRoles(), contains("my_second_realm_role"));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), aMapWithSize(1));
        assertThat("subgroup's client roles is null", subGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_second_client_role")));


        List<GroupRepresentation> innerSubGroups = subGroup.getSubGroups();
        assertThat("inner subgroups is empty", innerSubGroups, is(hasSize(1)));

        GroupRepresentation innerSubGroup = innerSubGroups.stream()
                .filter(s -> Objects.equals(s.getName(), "My second inner SubGroup"))
                .findFirst()
                .orElse(null);
        assertThat("inner subgroup is null", innerSubGroup, is(not(nullValue())));
        assertThat("inner subgroup's name not equal", innerSubGroup.getName(), is("My second inner SubGroup"));
        assertThat("inner subgroup's path not equal", innerSubGroup.getPath(), is("/My Group/My SubGroup/My second inner SubGroup"));
        assertThat("inner subgroup's attributes is null", innerSubGroup.getAttributes(), aMapWithSize(0));
        assertThat("inner subgroup's realm roles is null", innerSubGroup.getRealmRoles(), hasSize(0));
        assertThat("inner subgroup's client roles is null", innerSubGroup.getClientRoles(), aMapWithSize(0));
        assertThat("inner subgroup's subgroups is null", innerSubGroup.getSubGroups(), hasSize(0));
    }

    @Test
    @Order(43)
    public void shouldUpdateRealmUpdateGroupDeleteSubGroup() {
        doImport("42_update_realm_update_group_delete_subgroup.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation updatedGroup = loadGroup("/My Group");
        assertThat("name not equal", updatedGroup.getName(), is("My Group"));
        assertThat("path not equal", updatedGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", updatedGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", updatedGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", updatedGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", updatedGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", updatedGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        assertThat(updatedGroup.getSubGroups(), hasSize(0));

    }

    @Test
    @Order(44)
    public void shouldUpdateRealmAddGroupWithSubstringOfExistingGroupName() {
        doImport("43_update_realm_add_group_with_substring_of_existing_group_name.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation existingGroup = loadGroup("/My Group");
        assertThat("name not equal", existingGroup.getName(), is("My Group"));
        assertThat("path not equal", existingGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", existingGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", existingGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", existingGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", existingGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", existingGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        assertThat(existingGroup.getSubGroups(), hasSize(0));

        GroupRepresentation newGroup = loadGroup("/My");
        assertThat("name not equal", newGroup.getName(), is("My"));
        assertThat("path not equal", newGroup.getPath(), is("/My"));

        assertThat("attributes roles is null", newGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", newGroup.getAttributes(),
                hasEntry(is("my"), containsInAnyOrder("my value")));
    }

    @Test
    @Order(45)
    public void shouldUpdateRealmUpdateGroupWithSubstringOfExistingGroupName() {
        doImport("44_update_realm_update_group_with_substring_of_existing_group_name.json");

        RealmRepresentation createdRealm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();
        assertThat(createdRealm.getRealm(), is(REALM_NAME));
        assertThat(createdRealm.isEnabled(), is(true));

        GroupRepresentation existingGroup = loadGroup("/My Group");
        assertThat("name not equal", existingGroup.getName(), is("My Group"));
        assertThat("path not equal", existingGroup.getPath(), is("/My Group"));

        assertThat("attributes roles is null", existingGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", existingGroup.getAttributes(),
                hasEntry(is("my changed attribute"), containsInAnyOrder("my changed attribute value")));
        assertThat("realm roles is null", existingGroup.getRealmRoles(), hasSize(0));
        assertThat("client roles is null", existingGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", existingGroup.getClientRoles(), hasEntry(is("second-moped-client"), containsInAnyOrder("my_client_role_of_second-moped-client", "my_second_client_role_of_second-moped-client")));

        assertThat(existingGroup.getSubGroups(), hasSize(0));

        GroupRepresentation newGroup = loadGroup("/My");
        assertThat("name not equal", newGroup.getName(), is("My"));
        assertThat("path not equal", newGroup.getPath(), is("/My"));
        assertThat("attributes roles is null", newGroup.getAttributes(), aMapWithSize(1));
        assertThat("attributes roles is null", newGroup.getAttributes(),
                hasEntry(is("my"), containsInAnyOrder("my value")));
        assertThat("client roles is null", newGroup.getClientRoles(), aMapWithSize(1));
        assertThat("client roles is null", newGroup.getClientRoles(), hasEntry(is("moped-client"), containsInAnyOrder("my_client_role")));
    }

    @Test
    @Order(98)
    public void shouldUpdateRealmDeleteGroup() {
        GroupRepresentation updatedGroup = tryToLoadGroup("/My Added Group").get();
        assertThat(updatedGroup.getName(), Matchers.is(Matchers.equalTo("My Added Group")));

        GroupRepresentation updatedGroup2 = tryToLoadGroup("/My Group").get();
        assertThat(updatedGroup2.getName(), Matchers.is(Matchers.equalTo("My Group")));

        doImport("98_update_realm_delete_group.json");

        RealmRepresentation realm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();

        assertThat(realm.getRealm(), is(REALM_NAME));

        assertThat(tryToLoadGroup("/My Added Group").isPresent(), is(false));
        assertThat(tryToLoadGroup("/My Group").isPresent(), is(true));
    }

    @Test
    @Order(99)
    public void shouldUpdateRealmDeleteAllGroups() {
        doImport("99_update_realm_delete_all_groups.json");

        RealmRepresentation realm = keycloakProvider.get().realm(REALM_NAME).toRepresentation();

        assertThat(realm.getRealm(), is(REALM_NAME));
        assertThat(realm.getGroups(), is(nullValue()));
    }

    private GroupRepresentation loadGroup(String groupPath) {
        GroupsResource groupsResource = keycloakProvider.get()
                .realm(REALM_NAME)
                .groups();

        GroupRepresentation groupRepresentation = groupsResource
                .groups()
                .stream()
                .filter(g -> Objects.equals(groupPath, g.getPath()))
                .findFirst()
                .orElseThrow(() -> new KeycloakRepositoryException(
                        MessageFormat.format("Can't find group '{0}'.", groupPath)
                ));

        return groupsResource
                .group(groupRepresentation.getId())
                .toRepresentation();
    }

    private Optional<GroupRepresentation> tryToLoadGroup(String groupPath) {
        Optional<GroupRepresentation> maybeGroup;

        try {
            GroupRepresentation user = loadGroup(groupPath);

            maybeGroup = Optional.of(user);
        } catch (KeycloakRepositoryException e) {
            maybeGroup = Optional.empty();
        }

        return maybeGroup;
    }
}

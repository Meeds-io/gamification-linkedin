/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import LinkedInAdminConnectorItem from './components/LinkedInAdminConnectorItem.vue';
import LinkedInAdminHookFormDrawer from './components/LinkedInAdminHookFormDrawer.vue';
import LinkedInAdminConnectionSettingDrawer from './components/LinkedInAdminConnectionSettingDrawer.vue';
import LinkedInAdminConnectorHookList from './components/LinkedInAdminConnectorHookList.vue';
import LinkedInAdminConnectorHook from './components/LinkedInAdminConnectorHook.vue';
import LinkedInAdminConnectorHookDetail from './components/LinkedInAdminConnectorHookDetail.vue';
import LinkedInAdminConnectorEventItem from './components/LinkedInAdminConnectorEventItem.vue';
import LinkedInAdminConnectorEventList from './components/LinkedInAdminConnectorEventList.vue';

const components = {
  'linkedin-admin-connector-item': LinkedInAdminConnectorItem,
  'linkedin-admin-hook-form-drawer': LinkedInAdminHookFormDrawer,
  'linkedin-admin-connection-setting-drawer': LinkedInAdminConnectionSettingDrawer,
  'linkedin-admin-connector-hook-list': LinkedInAdminConnectorHookList,
  'linkedin-admin-connector-hook': LinkedInAdminConnectorHook,
  'linkedin-admin-connector-hook-detail': LinkedInAdminConnectorHookDetail,
  'linkedin-admin-connector-event-item': LinkedInAdminConnectorEventItem,
  'linkedin-admin-connector-event-list': LinkedInAdminConnectorEventList,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
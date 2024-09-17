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
export function init() {
  extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
    id: 'linkedin',
    name: 'linkedin',
    icon: 'fab fa-linkedin',
    iconColorClass: 'light-blue--text text--darken-4',
    title: 'LinkedIn',
    description: 'gamification.admin.linkedIn.label.description',
    rank: 50,
    comingSoon: false,
    init: () => {
      const lang = window.eXo?.env?.portal?.language || 'en';
      const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.LinkedInWebHookManagement-${lang}.json`;
      return exoi18n.loadLanguageAsync(lang, url);
    }
  });
}
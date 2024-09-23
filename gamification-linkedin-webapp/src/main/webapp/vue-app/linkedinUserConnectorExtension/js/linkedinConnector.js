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
export default {
  name: 'linkedin',
  title: 'linkedinConnector.label.profile',
  description: 'linkedinConnector.label.description',
  icon: 'fab fa-linkedin',
  iconColorClass: 'light-blue--text text--darken-4',
  initialized: true,
  identifier: '',
  user: '',
  rank: 50,
  PROFILE_BASER_URL: 'https://www.linkedin.com/in',
  init: () => {
    const lang = window.eXo?.env?.portal?.language || 'en';
    const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.LinkedInWebHookManagement-${lang}.json`;
    return exoi18n.loadLanguageAsync(lang, url);
  },
  openOauthPopup(connector) {
    const width = 500;
    const height = 600;
    const left = window.innerWidth / 2 - width / 2;
    const top = window.innerHeight / 2 - height / 2;
    const authUrl = `https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=${connector.apiKey}&redirect_uri=${connector.redirectUrl}&response_type=code&scope=r_basicprofile`;
    return window.open(authUrl, 'LinkedIn OAuth', `width=${width}, height=${height}, left=${left}, top=${top}`);
  },
};

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
  extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
    type: 'linkedin',
    options: {
      rank: 500,
      icon: 'fab fa-linkedin',
      iconColorClass: 'light-blue--text text--darken-4',
      match: (actionLabel) => [
        'postLiked',
        'postComment',
        'repost',
        'mention',
        'workFor',
        'followCompany',
      ].includes(actionLabel),
      getLink: (realization) => {
        try {
          const objId = JSON.parse(realization.objectId);
          realization.link = objId.stringUrl;
          return realization.link;
        } catch (e) {
          return null;
        }
      },
      isExtensible: false
    },
  });
}
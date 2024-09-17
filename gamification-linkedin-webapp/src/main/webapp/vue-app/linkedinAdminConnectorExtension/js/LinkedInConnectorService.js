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
export function searchOrganizations(keyword) {
  return fetch(`/gamification-linkedin/rest/remote/organization/lookup/?keyword=${keyword || ''}`  , {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else if (resp.status === 404 || resp.status === 401) {
      return resp.json().then((data) => {
        throw new Error(data.message);
      });
    } else {
      throw new Error('Error when getting linkedin companies');
    }
  });
}

export function getProjects(accessToken, hookId) {
  return fetch(`/gamification-linkedin/rest/linkedin/hooks/companies?accessToken=${accessToken || ''}&hookId=${hookId || ''}`  , {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else if (resp.status === 404 || resp.status === 401) {
      return resp.json().then((data) => {
        throw new Error(data.message);
      });
    } else {
      throw new Error('Error when getting linkedin companies');
    }
  });
}

export function getLinkedInWebHooks(offset, limit, includeLanguages, forceUpdate) {
  return fetch(`/gamification-linkedin/rest/linkedin/hooks?offset=${offset || 0}&limit=${limit|| 10}&includeLanguages=${includeLanguages|| false}&forceUpdate=${forceUpdate|| false}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting linkedin webhooks');
    }
  });
}

export function getLinkedInWebHookById(hookId) {
  return fetch(`/gamification-linkedin/rest/linkedin/hooks/${hookId}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting linkedin webhook');
    }
  });
}

export function getWebHookDirectories(projectId, offset, limit) {
  return fetch(`/gamification-linkedin/rest/linkedin/hooks/${projectId}/directories?offset=${offset || 0}&limit=${limit|| 25}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting linkedin webhook directories');
    }
  });
}

export function saveLinkedInWebHook(company, accessToken) {
  const formData = new FormData();
  formData.append('projectId', company.id);
  formData.append('projectName', company.name);
  formData.append('projectLogo', company.logo);
  formData.append('accessToken', accessToken);
  return fetch('/gamification-linkedin/rest/linkedin/hooks', {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams(formData).toString(),
  }).then(resp => {
    if (resp && resp.ok) {
      return resp.json;
    } else if (resp.status === 404 || resp.status === 401) {
      return resp.json().then((data) => {
        throw new Error(data.message);
      });
    } else {
      throw new Error('Error when saving linkedin webhook');
    }
  });
}

export function updateWebHookAccessToken(webHookId, accessToken) {
  const formData = new FormData();
  formData.append('webHookId', webHookId);
  formData.append('accessToken', accessToken);
  return fetch('/gamification-linkedin/rest/linkedin/hooks', {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams(formData).toString(),
  }).then(resp => {
    if (resp && resp.ok) {
      return resp.json;
    } else {
      throw new Error('Error when updating linkedin webhook access token');
    }
  });
}

export function deleteLinkedInWebHook(projectId) {
  return fetch(`/gamification-linkedin/rest/linkedin/hooks/${projectId}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (resp && resp.ok) {
      return resp.json;
    } else {
      throw new Error('Error when deleting linkedin webhook');
    }
  });
}
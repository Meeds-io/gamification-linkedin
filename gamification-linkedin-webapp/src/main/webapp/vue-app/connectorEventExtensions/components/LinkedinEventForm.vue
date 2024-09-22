<!--
This file is part of the Meeds project (https://meeds.io/).

Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <v-app>
    <v-card-text class="px-0 dark-grey-color font-weight-bold">
      {{ $t('gamification.linkedin.event.form.post.url') }}
    </v-card-text>
    <v-card-text class="ps-0 pt-2">
      <input
          ref="connectorApiKey"
          v-model="postURL"
          :placeholder="$t('linkedinConnector.admin.label.post.url.placeholder')"
          type="text"
          class="ignore-vuetify-classes full-width"
          required
          @input="readySelection">
    </v-card-text>

  </v-app>
</template>

<script>
export default {
  props: {
    properties: {
      type: Object,
      default: null
    },
    trigger: {
      type: String,
      default: null
    },
  },
  data() {
    return {
      offset: 0,
      limit: 500,
      postURL: null,
      projects: [],
      selected: null,
      directories: [],
      selectedDirectories: [],
      languages: [],
      selectedLanguages: [],
      value: null,
      loadingProjects: true,
      anyDir: false,
      anyLanguage: false,
      hasMore: false,
      allowOnlyHuman: true,
      selectedDirectoriesToDisplay: []
    };
  },
  computed: {
    needVerificationIsHuman() {
      return this.trigger !== 'stringCommentCreated';
    },
    directoryIds() {
      return this.properties?.directoryIds?.split(',').map(Number);
    },
  },
  watch: {
    value() {
      this.selected = this.projects[this.value];
      if (this.selected) {
        this.retrieveDirectories();
      }
    },
  },
  created() {
    console.log('LinkedinEventForm created');
  },
  methods: {
    retrieveProjects() {
      this.loadingProjects = true;
      return this.$linkedinConnectorService.getLinkedinWebHooks(null, null, true)
        .then(data => {
          this.projects = data.webhooks;
        }).finally(() => {
          if (this.properties) {
            this.selected = this.projects.find(r => Number(r.projectId) === Number(this.properties.projectId));
            this.value = this.projects.indexOf(this.selected);
            this.anyDir = !this.properties?.directoryIds;
            this.anyLanguage = !this.properties?.languageIds;
            this.allowOnlyHuman = this.properties?.mustBeHuman === 'true' || true;
          } else if (this.projects.length > 0) {
            this.selected = this.projects[0];
            this.value = this.projects.indexOf(this.selected);
            this.anyDir = true;
            this.anyLanguage = true;
            this.readySelection();
          }

          this.languages = this.selected?.languages;

          if (this.properties?.languageIds) {
            const selectedLanguagesArray = this.properties?.languageIds?.split(',');
            this.languages.forEach(rep => {
              if (selectedLanguagesArray.includes(rep.id)) {
                if (!this.selectedLanguages.some(c => (c?.id === rep.id) || (c === rep.id))) {
                  this.selectedLanguages.push(rep.id);
                }
              }
            });
          }

          this.loadingProjects = false;
        });
    },
    retrieveDirectories() {
      const offset = this.offset || 0;
      const limit = this.limit || 500;
      return this.$linkedinConnectorService.getWebHookDirectories(this.selected?.projectId, offset, limit)
        .then(data => {
          this.directories.push(...data);

          if (this.properties?.directoryIds) {
            const selectedDirsArray = this.properties?.directoryIds?.split(',');
            this.directories.forEach(rep => {
              if (selectedDirsArray.map(str => Number(str)).includes(Number(rep.id))) {
                if (!this.selectedDirectories.some(c => (c?.id === rep.id) || (c === rep.id))) {
                  this.selectedDirectories.push(rep.id);
                }
              }
            });
            this.selectedDirectoriesToDisplay = this.$linkedinUtils.processItems(this.directories, this.selectedDirectories);
          }
        });
    },
    readySelection() {
      const eventProperties = {
        projectId: this.selected?.projectId.toString(),
        mustBeHuman: this.allowOnlyHuman
      };

      document.dispatchEvent(new CustomEvent('event-form-filled', {detail: eventProperties}));
    },
    changeDirectorySelection() {
      if (!this.anyDir) {
        this.$root.$emit('directory-selection-drawer-open');
      } else {
        document.dispatchEvent(new CustomEvent('event-form-filled'));}
    },
    selectDirectories(directories) {
      if (directories.length > 0) {
        this.selectedDirectories = directories;
        this.selectedDirectoriesToDisplay = this.$linkedinUtils.processItems(this.directories, this.selectedDirectories);
        this.readySelection();
      } else {
        this.anyDir = true;
        document.dispatchEvent(new CustomEvent('event-form-filled'));
      }
    },
    changeLanguageSelection() {
      this.selectedLanguages = [];
      if (this.anyLanguage) {
        this.readySelection();
      } else {
        document.dispatchEvent(new CustomEvent('event-form-unfilled'));
      }
    },
    selectProject(project) {
      this.allowOnlyHuman = true;
      this.offset = 0;
      this.directories = [];
      this.selectedDirectories = [];
      this.selectedLanguages = [];
      this.anyDir = true;
      this.anyLanguage = true;
      this.selected = project;
      this.readySelection();
    },
  }
};
</script>
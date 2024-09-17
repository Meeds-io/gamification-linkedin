const path = require('path');
const { merge } = require('webpack-merge');
const webpackCommonConfig = require('./webpack.common.js');

const config = merge(webpackCommonConfig, {
  mode: 'production',
  entry: {
    engagementCenterExtensions: './src/main/webapp/vue-app/engagementCenterExtensions/extensions.js',
    connectorExtensions: './src/main/webapp/vue-app/connectorExtensions/extensions.js',
    linkedinAdminConnectorExtension: './src/main/webapp/vue-app/linkedinAdminConnectorExtension/extension.js',
    linkedinUserConnectorExtension: './src/main/webapp/vue-app/linkedinUserConnectorExtension/extension.js'
  },
  output: {
    path: path.join(__dirname, 'target/gamification-linkedin/'),
    filename: 'js/[name].bundle.js',
    libraryTarget: 'amd'
  }
});

module.exports = config;

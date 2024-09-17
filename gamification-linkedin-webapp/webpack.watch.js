const path = require('path');
const { merge } = require('webpack-merge');
const webpackCommonConfig = require('./webpack.prod.js');

// the display name of the war
const app = 'gamification-linkedin';

const exoServerPath = "/Users/serge/Downloads/meeds-community-7.0.x-meed-SNAPSHOT";

let config = merge(webpackCommonConfig, {
  output: {
    path: path.resolve(`${exoServerPath}/webapps/${app}/`)
  },
  mode: 'development',
  devtool: 'eval-source-map'
});
module.exports = config;
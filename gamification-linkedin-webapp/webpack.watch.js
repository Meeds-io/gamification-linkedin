const path = require('path');
const { merge } = require('webpack-merge');
const webpackCommonConfig = require('./webpack.prod.js');

// the display name of the war
const app = 'gamification-linkedin';

const exoServerPath = '/exo-server';

let config = merge(webpackCommonConfig, {
  output: {
    path: path.resolve(`${exoServerPath}/webapps/${app}/`)
  },
  mode: 'development',
  devtool: 'eval-source-map'
});
module.exports = config;
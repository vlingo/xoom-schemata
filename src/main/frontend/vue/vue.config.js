const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin')

module.exports = {
  publicPath: '/app/',
  devServer: {
    proxy: {
      '/organizations': {
        target: 'http://localhost:9019/'
      },
      '/schema': {
        target: 'http://localhost:9019/'
      },
      '/categories': {
        target: 'http://localhost:9019/'
      },
      '/code': {
        target: 'http://localhost:9019/'
      }
    },
  },
  productionSourceMap: false,
  configureWebpack: {
    optimization: {
      splitChunks: {
        minSize: 10000,
        maxSize: 786432,
      }
    }
  },
  chainWebpack: config => {
    config.plugin('monaco-editor').use(MonacoWebpackPlugin, [
      {
        languages: ['javascript', 'markdown', 'java']
      }
    ])
  }
}

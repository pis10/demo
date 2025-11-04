const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');
const dotenv = require('dotenv');

// 加载环境变量
const dotenvVars = dotenv.config().parsed || {};

// 只注入 VUE_APP_ 前缀的环境变量（安全白名单）
const envKeys = Object.keys(dotenvVars)
  .filter(key => key.startsWith('VUE_APP_'))
  .reduce((prev, next) => {
    prev[`process.env.${next}`] = JSON.stringify(dotenvVars[next]);
    return prev;
  }, {});

module.exports = (webpackEnv, argv) => {
  const isDevelopment = argv.mode === 'development';

  return {
    entry: './src/main.js',
    output: {
      path: path.resolve(__dirname, 'dist'),
      filename: isDevelopment ? '[name].js' : '[name].[contenthash].js',
      clean: true,
      publicPath: '/'
    },
    resolve: {
      extensions: ['.js', '.vue', '.json'],
      alias: {
        '@': path.resolve(__dirname, 'src'),
        'vue': 'vue/dist/vue.esm-bundler.js'
      }
    },
    module: {
      rules: [
        {
          test: /\.vue$/,
          loader: 'vue-loader'
        },
        {
          test: /\.js$/,
          exclude: /node_modules/,
          use: {
            loader: 'babel-loader',
            options: {
              presets: ['@babel/preset-env']
            }
          }
        },
        {
          test: /\.css$/,
          use: ['vue-style-loader', 'css-loader']
        },
        {
          test: /\.scss$/,
          use: ['vue-style-loader', 'css-loader', 'sass-loader']
        },
        {
          test: /\.(png|jpe?g|gif|svg)$/i,
          type: 'asset/resource'
        }
      ]
    },
    plugins: [
      new VueLoaderPlugin(),
      new HtmlWebpackPlugin({
        template: './index.html',
        inject: true
      }),
      new webpack.DefinePlugin({
        ...envKeys,
        __VUE_OPTIONS_API__: true,
        __VUE_PROD_DEVTOOLS__: false,
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false
      })
    ],
    devServer: {
      port: 5174,
      hot: true,
      historyApiFallback: true,
      client: {
        // 在 VULN 模式演示中，XSS 代码可能产生运行时错误
        // 禁用 overlay 防止干扰演示效果
        overlay: {
          errors: false,
          warnings: false,
          runtimeErrors: false,
        },
      },
      static: {
        directory: path.join(__dirname, 'public'),
        publicPath: '/'
      },
      proxy: [
        {
          context: ['/api'],
          target: 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
          ws: true,
          onProxyReq: function(proxyReq, req, res) {
            proxyReq.setHeader('Origin', 'http://localhost:8080');
            proxyReq.setHeader('Host', 'localhost:8080');
          }
        }
      ]
    },
    devtool: isDevelopment ? 'eval-source-map' : 'source-map',
    optimization: {
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: 'vendors',
            priority: 10
          }
        }
      }
    }
  };
};

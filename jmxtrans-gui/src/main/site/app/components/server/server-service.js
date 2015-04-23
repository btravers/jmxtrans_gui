'use strict';

var app = angular.module('jmxtransGui');

app.service('serverService', function ($http) {

  var list = [];

  this.getList = function () {
    return list;
  };

  this.updateServersList = function () {
    var req = {
      method: 'GET',
      url: 'server/all'
    };

    $http(req)
      .success(function (response) {
        list = response;
      })
      .error(function () {

      });
  };

  this.delete = function (host, port) {
    var req = {
      method: 'DELETE',
      url: 'server',
      params: {
        host: host,
        port: port
      }
    };

    $http(req)
      .success(function () {
        setTimeout(function () {
          updateServersList();
        }, 1000);
      })
      .error(function () {

      });
  };

  this.get = function (host, port) {
    var req = {
      method: 'GET',
      url: 'server',
      params: {
        host: host,
        port: port
      }
    };

    $http(req)
      .success(function (response) {
        var server = {};
        server.id = response.id;
        server.server = response.source.servers[0];

        return server;
      })
      .error(function () {

      });
  };

});

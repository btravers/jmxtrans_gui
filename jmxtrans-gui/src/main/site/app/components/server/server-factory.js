'use strict';

var app = angular.module('jmxtransGui');

app.factory('serverFactory', function ($http, serverService, writerService) {

  var factory = {};

  factory.id = null;
  factory.server = {
    port: null,
    host: null,
    queries: []
  };
  factory.saved = false;

  factory.blankQuery = null;
  factory.blankAttr = [];
  factory.blankTypeNames = [];

  factory.errorMessage = {};

  factory.loadJMXTree = function () {
    if (factory.server.host && factory.server.port) {
      var req = {
        method: 'GET',
        url: 'refresh',
        params: {
          host: factory.server.host,
          port: factory.server.port
        }
      };

      $http(req)
        .error(function () {

        });
    }
  };

  factory.save = function () {
    if (!factory.saved) {
      if (factory.blankQuery && factory.blankQuery.obj) {
        factory.server.queries.push(factory.blankQuery);
        factory.blankQuery = null;
      }

      angular.forEach(this.blankAttr, function (attr, i) {
        if (attr && attr.value) {
          if (!factory.server.queries[i].attr) {
            factory.server.queries[i].attr = [];
          }
          factory.queries[i].attr.push(attr.value);
          factory.blankAttr[i] = null;
        }
      });

      angular.forEach(factory.blankTypeNames, function (typeName, i) {
        if (typeName && typeName.value) {
          if (!factory.server.queries[i].typeNames) {
            factory.server.queries[i].typeNames = [];
          }
          factory.server.queries[i].typeNames.push(typeName.value);
          factory.blankTypeNames[i] = null;
        }
      });

      if (factory.id) {
        var req = {
          method: 'POST',
          url: 'server/_update',
          params: {
            id: factory.id
          },
          data: {
            servers: [factory.server]
          }
        };

        $http(req)
          .success(function () {
            factory.saved = true;
            setTimeout(function () {
              serverService.display(factory.server.host, factory.server.port);
            }, 1000);
          })
          .error(function (response) {
            angular.forEach(response, function (message) {
              factory.errorMessage[message.field] = message.message;
            }, factory);
          });
      } else {
        var req = {
          method: 'POST',
          url: 'server',
          data: {
            servers: [factory.server]
          }
        };

        $http(req)
          .success(function () {
            setTimeout(function () {
              serverService.display(factory.server.host, factory.server.port);
            }, 1000);
          })
          .error(function (response) {
            angular.forEach(response, function (message) {
              factory.errorMessage[message.field] = message.message;
            }, factory);
          });
      }
    }
  };

  factory.removeQuery = function (index) {
    factory.server.queries.splice(index, 1);
    factory.saved = false;
  };

  factory.addBlankQuery = function () {
    if (factory.blankQuery && factory.blankQuery.obj) {
      factory.server.queries.push(factory.blankQuery);
    }

    factory.blankQuery = {
      obj: null,
      attr: [],
      typeNames: [],
      outputWriters: [
        writerService.get()
      ]
    };
  };

  factory.showErrorMessage = function (key) {
    return key in factory.errorMessage;
  };

  return factory;

});

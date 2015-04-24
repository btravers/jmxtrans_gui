'use strict';

var app = angular.module('jmxtransGui');

app.factory('serverFactory', function ($rootScope, $http, writerService, configService) {

  var Server = function() {
    var ref = this;

    this.id = null;

    this.server = {
      port: null,
      host: null,
      queries: []
    };
    this.saved = false;

    this.blankQuery = null;
    this.blankAttr = [];
    this.blankTypeNames = [];

    this.errorMessage = {};

    this.removeQuery = function (index) {
      this.server.queries.splice(index, 1);
      this.saved = false;
    };

    this.addBlankQuery = function () {
      if (this.blankQuery && this.blankQuery.obj) {
        this.server.queries.push(this.blankQuery);
      }

      this.blankQuery = {
        obj: null,
        attr: [],
        typeNames: [],
        outputWriters: [
          writerService.get()
        ]
      };
    };

    this.loadJMXTree = function () {
      if (this.server.host && this.server.port) {
        var req = {
          method: 'GET',
          url: 'refresh',
          params: {
            host: this.server.host,
            port: this.server.port
          }
        };

        req.url = configService.getUrl() + req.url;

        $http(req)
          .error(function () {

          });
      }
    };

    this.save = function () {
      if (!this.saved) {
        if (this.blankQuery && this.blankQuery.obj) {
          this.server.queries.push(this.blankQuery);
          this.blankQuery = null;
        }

        angular.forEach(this.blankAttr, function (attr, i) {
          if (attr && attr.value) {
            if (!ref.server.queries[i].attr) {
              ref.server.queries[i].attr = [];
            }
            ref.server.queries[i].attr.push(attr.value);
            ref.blankAttr[i] = null;
          }
        });

        angular.forEach(this.blankTypeNames, function (typeName, i) {
          if (typeName && typeName.value) {
            if (!ref.server.queries[i].typeNames) {
              ref.server.queries[i].typeNames = [];
            }
            ref.server.queries[i].typeNames.push(typeName.value);
            ref.blankTypeNames[i] = null;
          }
        });

        if (this.id) {
          var req = {
            method: 'POST',
            url: 'server/_update',
            params: {
              id: this.id
            },
            data: {
              servers: [this.server]
            }
          };

          req.url = configService.getUrl() + req.url;

          $http(req)
            .success(function () {
              ref.saved = true;
              setTimeout(function () {
                $rootScope.$broadcast('update', {
                  host: ref.server.host,
                  port: ref.server.port
                });
              }, 1000);
            })
            .error(function (response) {
              angular.forEach(response, function (message) {
                ref.errorMessage[message.field] = message.message;
              });
            });
        } else {
          var req = {
            method: 'POST',
            url: 'server',
            data: {
              servers: [this.server]
            }
          };

          req.url = configService.getUrl() + req.url;

          $http(req)
            .success(function () {
              setTimeout(function () {
                $rootScope.$broadcast('update', {
                  host: ref.server.host,
                  port: ref.server.port
                });
              }, 1000);
            })
            .error(function (response) {
              angular.forEach(response, function (message) {
                ref.errorMessage[message.field] = message.message;
              });
            });
        }
      }
    };

    this.showErrorMessage = function (key) {
      return key in this.errorMessage;
    };
  };

  return Server;
});

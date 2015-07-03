(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .service('serverService', serverService);

  function serverService($http, $q, configService) {
    this.findAllHostsAndPorts = findAllHostsAndPorts;
    this.getServer = getServer;
    this.deleteServer = deleteServer;
    this.updateServer = updateServer;
    this.saveServer = saveServer;

    function findAllHostsAndPorts() {
      var req = {
        method: 'GET',
        url: 'server/all'
      };

      req.url = configService.getUrl() + req.url;

      var list = $q.defer();

      $http(req)
        .success(function (response) {
          list.resolve(response);
        })
        .error(function () {
          list.reject();
        });

      return list.promise;
    }

    function getServer(host, port) {
      var req = {
        method: 'GET',
        url: 'server',
        params: {
          host: host,
          port: port
        }
      };

      req.url = configService.getUrl() + req.url;

      var server = $q.defer();

      $http(req)
        .success(function (response) {
          server.resolve({
            id: response.id,
            server: response.source.servers[0]
          });
        })
        .error(function () {
          server.reject();
        }
      );

      return server.promise;
    }

    function deleteServer(host, port) {
      var req = {
        method: 'DELETE',
        url: 'server',
        params: {
          host: host,
          port: port
        }
      };

      req.url = configService.getUrl() + req.url;

      var promise = $q.defer();

      $http(req)
        .success(function () {
          promise.resolve();
        })
        .error(function () {
          promise.reject();
        });

      return promise.promise;
    }

    function updateServer(id, server) {
      var req = {
        method: 'POST',
        url: 'server/_update',
        params: {
          id: id
        },
        data: {
          servers: [server]
        }
      };

      req.url = configService.getUrl() + req.url;

      var result = $q.defer();

      $http(req)
        .success(function () {
          result.resolve();
        })
        .error(function (response) {
          result.reject(response);
        });

      return result.promise;
    }

    function saveServer(server) {
      var req = {
        method: 'POST',
        url: 'server',
        data: {
          servers: [server]
        }
      };

      req.url = configService.getUrl() + req.url;

      var result = $q.defer();

      $http(req)
        .success(function () {
          result.resolve();
        })
        .error(function (response) {
          result.reject(response);
        });

      return result.promise;
    }
  }
})();

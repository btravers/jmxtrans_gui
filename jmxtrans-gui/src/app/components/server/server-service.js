(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .service('serverService', serverService);

  function serverService($http, $q, configService, ngToast) {
    this.findAllHostsAndPorts = findAllHostsAndPorts;
    this.getServer = getServer;
    this.deleteServer = deleteServer;

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
          ngToast.create({
            className: 'danger',
            content: 'An error occurred when loading servers list'
          });
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
          ngToast.create({
            className: 'danger',
            content: 'An error occurred when loading server conf document'
          });
        }
      );

      return server.promise;
    }

    function deleteServer() {
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
          ngToast.create({
            className: 'success',
            content: "Delete server conf document successfully"
          });
          promise.resolve();
        })
        .error(function () {
          ngToast.create({
            className: 'danger',
            content: 'An error occurred during the deletion of the server conf document'
          });
        });

      return promise.promise;

    }
  }
})();

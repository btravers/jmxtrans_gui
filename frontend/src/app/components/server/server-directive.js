(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .directive('server', server);

  function server() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        server: '=server',
        alerts: '=alerts'
      },
      controller: controller,
      templateUrl: 'app/components/server/server.html'
    };

    function controller($scope, $http, configService) {
      $scope.valid = false;

      $scope.loadObjectnames = loadObjectnames;

      $scope.$watch(function($scope) {
        return $scope.server;
      }, function(server) {
        if (!server || !server.server.host || !server.server.port) {
          return;
        }

        var req = {
          method: 'GET',
          url: 'server/exist',
          params: {
            host:server.server.host,
            port: server.server.port,
            username: server.server.username,
            password: server.server.password
          }
        };

        req.url = configService.getUrl() + req.url;

        $http(req)
          .success(function (response) {
            $scope.valid = response
          })
          .error(function () {
            $scope.valid = false;
          });
      });

      function loadObjectnames() {
        if (!$scope.server.server.host || !$scope.server.server.port) {
          return;
        }

        var req = {
          method: 'GET',
          url: 'autocomplete/load',
          params: {
            host: $scope.server.server.host,
            port: $scope.server.server.port,
            username: $scope.server.server.username,
            password: $scope.server.server.password
          }
        };

        req.url = configService.getUrl() + req.url;

        $http(req)
          .success(function (response) {
            $scope.valid = response;
          })
          .error(function () {
            $scope.valid = false;
          });
      }

    }
  }
})();

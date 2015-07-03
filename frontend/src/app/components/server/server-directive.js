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

    function controller($scope, $http, suggestionService, configService) {
      $scope.validJMXHost = validJMXHost;
      $scope.loadObjectnames = loadObjectnames;

      function validJMXHost() {
        return suggestionService.getObjectNames() && suggestionService.getObjectNames().length != 0;
      }

      function loadObjectnames() {
        console.log('loadObjectnames');

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
          .success(function () {
            suggestionService.setObjectNames($scope.server.server.host, $scope.server.server.port);
          })
          .error(function () {

          });
      }

    }
  }
})();

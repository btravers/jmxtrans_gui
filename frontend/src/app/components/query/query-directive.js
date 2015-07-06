(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .directive('query', query);

  function query($http, configService) {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        query: '=query',
        server: '=server',
        queryIndex: '=index'
      },
      link: function (scope) {
        if (!scope.server.server.host || !scope.server.server.port) {
          return;
        }

        var req = {
          method: 'GET',
          url: 'autocomplete/attr',
          params: {
            host: scope.server.server.host,
            port: scope.server.server.port,
            name: scope.query.obj
          }
        };

        req.url = configService.getUrl() + req.url;

        $http(req)
          .success(function (response) {
            scope.attrSuggestions = [];
            response.sort();
            angular.forEach(response, function (suggestion) {
              this.push({
                value: suggestion
              });
            }, scope.attrSuggestions);
          })
          .error(function () {
            scope.attrSuggestions = []
          });
      },
      controller: controller,
      templateUrl: 'app/components/query/query.html'
    };

    function controller($scope, $http, configService, suggestionService) {

      $scope.attrSuggestions = [];

      $scope.getObjectNames = getObjectNames;
      $scope.suggestAttr = suggestAttr;

      $scope.addAttr = addAttr;
      $scope.removeAttr = removeAttr;
      $scope.nbTypeNames = nbTypeNames;

      function getObjectNames() {
        return suggestionService.getObjectNames();
      }

      function suggestAttr() {
        if (!$scope.server.server.host || !$scope.server.server.port) {
          return;
        }

        var req = {
          method: 'GET',
          url: 'autocomplete/attr',
          params: {
            host: $scope.server.server.host,
            port: $scope.server.server.port,
            name: $scope.query.obj
          }
        };

        req.url = configService.getUrl() + req.url;

        $scope.query.attr = [];

        $http(req)
          .success(function (response) {
            $scope.attrSuggestions = [];
            response.sort();
            angular.forEach(response, function (suggestion) {
              this.push({
                value: suggestion
              });
            }, $scope.attrSuggestions);
          })
          .error(function () {
            $scope.attrSuggestions = [];
          });
      }

      function addAttr() {
        if (!$scope.query.attr) {
          $scope.query.attr = [];
        }
        $scope.query.attr.push(null);
      }

      function removeAttr(index) {
        $scope.query.attr.splice(index, 1);
        $scope.server.currentForm.$setDirty();
      }

      function nbTypeNames() {
        var count = ($scope.query.obj.match(/\*/g) || []).length;

        if (!$scope.query.typeNames) {
          $scope.query.typeNames = [];
        }

        if ($scope.query.typeNames.length != count) {
          var tmp = $scope.query.typeNames;
          $scope.query.typeNames = [];

          for (var i = 0; i < count; i++) {
            if (i < tmp.length) {
              $scope.query.typeNames.push(tmp[i]);
            } else {
              $scope.query.typeNames.push('');
            }
          }
        }
      }
    }
  }
})();

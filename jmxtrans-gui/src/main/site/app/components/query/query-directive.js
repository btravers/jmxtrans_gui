(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .directive('query', query);

  function query() {
    return {
      require: 'server',
      restrict: 'E',
      replace: true,
      scope: {
        query: '=query',
        server: '=server',
        queryIndex: '=index'
      },
      controller: controller,
      templateUrl: 'app/components/query/query.html'
    };

    function controller($scope, $http, configService) {

      $scope.nameSuggestions = null;
      $scope.attrSuggestions = null;

      $scope.suggestName = suggestName;
      $scope.suggestAttr = suggestAttr;
      $scope.addBlankAttr = addBlankAttr;
      $scope.removeAttr = removeAttr;
      $scope.removeBlankAttr = removeBlankAttr;
      $scope.addBlankTypeName = addBlankTypeName;
      $scope.removeTypeName = removeTypeName;
      $scope.removeBlankTypeName = removeBlankTypeName;

      function suggestName() {
        if (!$scope.nameSuggestions) {
          var req = {
            method: 'GET',
            url: 'suggest_name',
            params: {
              host: $scope.server.server.host,
              port: $scope.server.server.port
            }
          };

          req.url = configService.getUrl() + req.url;

          $http(req)
            .success(function (response) {
              $scope.nameSuggestions = response;
            })
            .error(function () {

            });
        }

        $scope.attrSuggestions = null;
      }

      function suggestAttr() {
        if (!$scope.attrSuggestions) {
          var req = {
            method: 'GET',
            url: 'suggest_attr',
            params: {
              host: $scope.server.server.host,
              port: $scope.server.server.port,
              name: $scope.query.obj
            }
          };

          req.url = configService.getUrl() + req.url;

          $http(req)
            .success(function (response) {
              $scope.attrSuggestions = response;
            })
            .error(function () {

            });
        }
      }

      function addBlankAttr() {
        if ($scope.server.blankAttr[$scope.queryIndex] && $scope.server.blankAttr[$scope.queryIndex].value) {
          if (!$scope.query.attr) {
            $scope.query.attr = [];
          }
          $scope.query.attr.push($scope.server.blankAttr[$scope.queryIndex].value);
        }

        $scope.server.blankAttr[$scope.queryIndex] = {
          value: null
        };

        $scope.suggestAttr();
      }

      function removeAttr(index) {
        $scope.query.attr.splice(index, 1);
        $scope.server.saved = false;
      }

      function removeBlankAttr() {
        $scope.server.blankAttr[$scope.queryIndex] = null;
      }

      function addBlankTypeName() {
        if ($scope.server.blankTypeNames[$scope.queryIndex] && $scope.server.blankTypeNames[$scope.queryIndex].value) {
          if (!$scope.query.typeNames) {
            $scope.query.typeNames = [];
          }
          $scope.query.typeNames.push($scope.server.blankTypeNames[$scope.queryIndex].value);
        }

        $scope.server.blankTypeNames[$scope.queryIndex] = {
          value: null
        };
      }

      function removeTypeName(index) {
        $scope.query.typeNames.splice(index, 1);
        $scope.server.saved = false;
      }

      function removeBlankTypeName() {
        $scope.server.blankTypeNames[$scope.queryIndex] = null;
      }
    }
  }
})();

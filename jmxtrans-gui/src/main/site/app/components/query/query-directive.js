'use strict';

var app = angular.module('jmxtransGui');

app.directive('query', function () {
  return {
    require: 'server',
    restrict: 'E',
    replace: true,
    scope: {
      query: '=query',
      server: '=server',
      queryIndex: '=index'
    },
    controller: function ($scope, $http) {

      $scope.suggestName = function () {
        if (!$scope.nameSuggestions) {
          var req = {
            method: 'GET',
            url: 'suggest_name',
            params: {
              host: $scope.server.server.host,
              port: $scope.server.server.port
            }
          };

          $http(req)
            .success(function (response) {
              $scope.nameSuggestions = response;
            });
        }

        $scope.attrSuggestions = null;
      };

      $scope.suggestAttr = function () {
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

          $http(req)
            .success(function (response) {
              $scope.attrSuggestions = response;
            });
        }
      };

      $scope.addBlankAttr = function () {
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
      };

      $scope.removeAttr = function (index) {
        $scope.query.attr.splice(index, 1);
        $scope.server.saved = false;
      };

      $scope.removeBlankAttr = function () {
        $scope.server.blankAttr[$scope.queryIndex] = null;
      };

      $scope.addBlankTypeName = function () {
        if ($scope.server.blankTypeNames[$scope.queryIndex] && $scope.server.blankTypeNames[$scope.queryIndex].value) {
          if (!$scope.query.typeNames) {
            $scope.query.typeNames = [];
          }
          $scope.query.typeNames.push($scope.server.blankTypeNames[$scope.queryIndex].value);
        }

        $scope.server.blankTypeNames[$scope.queryIndex] = {
          value: null
        };
      };

      $scope.removeTypeName = function (index) {
        $scope.query.typeNames.splice(index, 1);
        $scope.server.saved = false;
      };

      $scope.removeBlankTypeName = function () {
        $scope.server.blankTypeNames[$scope.queryIndex] = null;
      };


    },
    templateUrl: 'app/components/query/query.html'
  };
});

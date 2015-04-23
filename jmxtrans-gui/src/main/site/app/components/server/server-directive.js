'use strict';

var app = angular.module('jmxtransGui');

app.directive('server', function () {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      server: '=server',
      alerts: '=alerts'
    },
    templateUrl: 'app/components/server/server.html'
  };
});

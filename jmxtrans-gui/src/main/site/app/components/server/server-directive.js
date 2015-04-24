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
      templateUrl: 'app/components/server/server.html'
    };
  }
})();

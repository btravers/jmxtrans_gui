(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .service('suggestionService', suggestionService);

  function suggestionService($http, configService, ngToast) {

    this.setObjectNames = setObjectNames;
    this.getObjectNames = getObjectNames;
    this.removeObjectNames = removeObjectNames;

    function setObjectNames(host, port) {
      var vm = this;

      if (!host || !port) {
        return;
      }

      var req = {
        method: 'GET',
        url: 'autocomplete/name',
        params: {
          host: host,
          port: port
        }
      };

      req.url = configService.getUrl() + req.url;

      $http(req)
        .success(function (response) {
          vm.objectNames = response || [];

          if (response.length == 0) {
            ngToast.create({
              className: 'danger',
              content: 'No MBeans have been found for this server'
            });
          }
        })
        .error(function () {
          vm.objectNames = [];
          ngToast.create({
            className: 'danger',
            content: 'An error occurred when loading server MBeans'
          });
        });
    }

    function getObjectNames() {
      return this.objectNames;
    }

    function removeObjectNames() {
      this.objectNames = [];
    }
  }
})();

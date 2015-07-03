(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .service('writerService', writerService);

  function writerService($http, $q, configService, ngToast) {

    this.get = get;
    this.set = set;

    function get() {
      var req = {
        method: 'GET',
        url: 'settings'
      };

      req.url = configService.getUrl() + req.url;

      var writer = $q.defer();

      $http(req)
        .success(function (response) {
          if (!response.settings) {
            response.settings = {};
          }
          writer.resolve(response);
        })
        .error(function () {
          writer.reject();
          ngToast.create({
            className: 'danger',
            content: 'An error when loading output writer configuration'
          });
        });

      return writer.promise;
    }

    function set(writer) {
      var req = {
        method: 'POST',
        url: 'settings',
        data: writer
      };

      req.url = configService.getUrl() + req.url;

      $http(req)
        .success(function () {
          ngToast.create({
            className: 'success',
            content: 'Save output writer configuration successfully'
          });
        })
        .error(function () {
          ngToast.create({
            className: 'danger',
            content: 'An error occurred when saving output writer configuration'
          });
        });
    }
  }
})();

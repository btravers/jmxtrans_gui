'use strict';

var app = angular.module('jmxtransGui');

app.service('writerService', function ($http, $q, configService, ngToast) {

  this.get = function () {
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
  };

  this.set = function (writer) {
    var req = {
      method: 'POST',
      url: 'settings',
      data: writer.writer
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
  };

});

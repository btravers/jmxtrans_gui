'use strict';

var app = angular.module('jmxtransGui');

app.service('writerService', function ($http) {

  this.get = function () {
    $http.get('settings')
      .success(function (data) {
        var writer = data;
        if (!writer.settings) {
          writer.settings = {};
        }
        return writer;
      })
      .error(function () {
        
      });
  };

  this.set = function (writer) {
    var req = {
      method: 'POST',
      url: 'settings',
      data: writer.writer
    };

    $http(req)
      .success(function () {

      })
      .error(function () {

      });
  };

});

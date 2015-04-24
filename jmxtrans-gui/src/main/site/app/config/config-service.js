'use strict';

var app = angular.module('jmxtransGui')

app.service('configService', function () {
  var url = 'http://localhost:8080/';

  this.getUrl = function () {
    return url;
  };

});

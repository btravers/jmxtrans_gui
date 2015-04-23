'use strict';

var app = angular.module('jmxtransGui');

app.directive('bluefloodWriterForm', function () {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      writer: '=writer'
    },
    templateUrl: 'app/components/blueflood-writer-form/blueflood-writer-form.html'
  };
});

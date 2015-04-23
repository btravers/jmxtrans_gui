'use strict';

var app = angular.module('jmxtransGui');

app.directive('dailyKeyOutWriterForm', function () {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      writer: '=writer'
    },
    templateUrl: 'app/components/daily-key-out-writer-form/daily-key-out-writer-form.html'
  };
});

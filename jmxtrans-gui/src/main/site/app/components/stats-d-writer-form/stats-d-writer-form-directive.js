'use strict';

var app = angular.module('jmxtransGui');

app.directive('statsDWriterForm', function () {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      writer: '=writer'
    },
    templateUrl: 'app/components/stats-d-writer-form/stats-d-writer-form.html'
  };
});

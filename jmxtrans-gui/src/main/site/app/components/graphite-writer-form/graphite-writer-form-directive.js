'use strict';

var app = angular.module('jmxtransGui');

app.directive('graphiteWriterForm', function () {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      writer: '=writer'
    },
    templateUrl: 'app/components/graphite-writer-form/graphite-writer-form.html'
  };
});

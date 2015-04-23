'use strict';

var app = angular.module('jmxtransGui');

app.directive('gangliaWriterForm', function () {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      writer: '=writer'
    },
    templateUrl: 'app/components/ganglia-writer-form/ganglia-writer-form.html'
  };
});

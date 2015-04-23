'use strict';

var app = angular.module('jmxtransGui');

app.directive('rrdToolWriterForm', function () {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      writer: '=writer'
    },
    templateUrl: 'app/components/rrd-tool-writer-form/rrd-tool-writer-form.html'
  };
});

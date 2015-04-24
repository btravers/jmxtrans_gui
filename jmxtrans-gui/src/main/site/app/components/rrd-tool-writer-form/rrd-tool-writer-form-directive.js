(function () {
  'use strict';

  angular.module('jmxtransGui')
    .directive('rrdToolWriterForm', rrdToolWriterForm);

  function rrdToolWriterForm() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        writer: '=writer'
      },
      templateUrl: 'app/components/rrd-tool-writer-form/rrd-tool-writer-form.html'
    };
  }
})();

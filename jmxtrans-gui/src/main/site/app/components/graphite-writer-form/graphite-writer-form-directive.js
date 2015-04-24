(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .directive('graphiteWriterForm', graphiteWriterForm);

  function graphiteWriterForm() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        writer: '=writer'
      },
      templateUrl: 'app/components/graphite-writer-form/graphite-writer-form.html'
    };
  }
})();

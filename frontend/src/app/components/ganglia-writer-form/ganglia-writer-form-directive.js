(function () {
  'use strict';

  angular
    .module('jmxtransGui')

    .directive('gangliaWriterForm', gangliaWriterForm);

  function gangliaWriterForm() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        writer: '=writer'
      },
      templateUrl: 'app/components/ganglia-writer-form/ganglia-writer-form.html'
    };
  }
})();

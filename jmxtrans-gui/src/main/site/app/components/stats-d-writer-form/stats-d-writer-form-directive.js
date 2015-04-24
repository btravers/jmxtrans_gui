(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .directive('statsDWriterForm', statsDWriterForm);

  function statsDWriterForm() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        writer: '=writer'
      },
      templateUrl: 'app/components/stats-d-writer-form/stats-d-writer-form.html'
    };
  }
})();

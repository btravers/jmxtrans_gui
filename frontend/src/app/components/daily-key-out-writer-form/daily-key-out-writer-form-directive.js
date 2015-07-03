(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .directive('dailyKeyOutWriterForm', dailyKeyOutWriterForm);

  function dailyKeyOutWriterForm() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        writer: '=writer'
      },
      templateUrl: 'app/components/daily-key-out-writer-form/daily-key-out-writer-form.html'
    };
  }
})();

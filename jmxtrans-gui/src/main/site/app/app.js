(function () {
  'use strict';

  angular
    .module('jmxtransGui', ['angularFileUpload', 'ui.bootstrap', 'ngToast'])
    .config(config);

  function config(ngToastProvider) {
    ngToastProvider.configure({
      timeout: 5000,
      dismissButton: true
    });
  }
})();

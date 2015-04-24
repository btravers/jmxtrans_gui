'use strict';

var app = angular.module('jmxtransGui', ['angularFileUpload', 'ui.bootstrap', 'ngToast']);

app.config(function(ngToastProvider) {
  ngToastProvider.configure({
    timeout: 5000,
    dismissButton: true
  });
});

(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .service('configService', configService);

  function configService() {
    var url = 'http://localhost:8080/';
    var reloadInterval = 60000;

    this.getUrl = getUrl;
    this.getReloadInterval = getReloadInterval;

    function getUrl() {
      return url;
    }

    function getReloadInterval() {
      return reloadInterval;
    }
  }
})();

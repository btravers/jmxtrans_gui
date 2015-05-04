(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .service('configService', configService);

  function configService($location) {
    var url;
    var reloadInterval = 60000;

    this.getUrl = getUrl;
    this.getReloadInterval = getReloadInterval;

    function getUrl() {
      if(!url) {
        if ($location.port() == 3000) {
          url = $location.host() + ':8080/';
        } else {
          url = '';
        }
      }

      return url;
    }

    function getReloadInterval() {
      return reloadInterval;
    }
  }
})();

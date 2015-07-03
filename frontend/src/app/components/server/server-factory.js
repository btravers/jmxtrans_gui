(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .factory('serverFactory', serverFactory);

  function serverFactory($rootScope, $http, serverService, writerService, configService, formErrorHandler, ngToast) {

    var Server = function () {
      var vm = this;

      vm.id = null;
      vm.currentForm = null;
      vm.server = {
        port: null,
        host: null,
        queries: []
      };
      vm.validJMXHost = false;

      vm.removeQuery = removeQuery;
      vm.addQuery = addQuery;
      vm.showErrorMessage = showErrorMessage;
      vm.save = save;


      function removeQuery(index) {
        if (index == vm.server.queries.length) {
          vm.blankQuery = null;
        } else {
          vm.server.queries.splice(index, 1);
          vm.currentForm.$setDirty();
        }
      }

      function addQuery() {
        var i = vm.server.queries.length;

        vm.server.queries.push({
          obj: null,
          attr: [],
          typeNames: [],
          outputWriters: []
        });

        writerService.get().then(function (writer) {
          vm.server.queries[i].outputWriters.push(writer);
        });
      }

      function save() {
        vm.errorMessage = {};

        for (var i = vm.server.queries.length - 1; i > -1; i--) {
          if (!vm.server.queries[i].obj) {
            vm.server.queries.splice(i, 1);
            continue;
          }

          for (var j = vm.server.queries[i].attr.length - 1; j > -1; j--) {
            if (!vm.server.queries[i].attr[j]) {
              vm.server.queries[i].attr.splice(j, 1);
            }
          }

          if (!vm.server.queries[i].attr.length) {
            vm.server.queries.splice(i, 1);
          }
        }

        if (vm.id) {
          serverService.updateServer(vm.id, vm.server).then(function () {
            vm.currentForm.$setPristine();
            ngToast.create({
              className: 'success',
              content: 'Save server conf document successfully'
            });
            $rootScope.$emit('update', {
              host: vm.server.host,
              port: vm.server.port
            });
          }, function (response) {
            formErrorHandler.setErrors(response);
            ngToast.create({
              className: 'danger',
              content: 'An error occurred when saving server conf document'
            });
          });
        } else {
          serverService.saveServer(vm.server).then(function () {
            vm.currentForm.$setPristine();
            ngToast.create({
              className: 'success',
              content: 'Save server conf document successfully'
            });
            $rootScope.$emit('update', {
              host: vm.server.host,
              port: vm.server.port
            });
          }, function (response) {
            formErrorHandler.setErrors(response);
            ngToast.create({
              className: 'danger',
              content: 'An error occurred when saving server conf document'
            });
          });
        }
      }

      function showErrorMessage(key) {
        return formErrorHandler.existError(key);
      }
    };

    return Server;
  }
})();

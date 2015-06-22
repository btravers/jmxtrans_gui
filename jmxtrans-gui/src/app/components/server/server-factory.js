(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .factory('serverFactory', serverFactory);

  function serverFactory($rootScope, $http, serverService, writerService, configService, ngToast) {

    var Server = function () {
      var vm = this;

      vm.id = null;
      vm.currentForm = null;
      vm.server = {
        port: null,
        host: null,
        queries: []
      };
      vm.blankQuery = null;
      vm.blankAttr = [];
      vm.blankTypeNames = [];
      vm.errorMessage = {};
      vm.validJMXHost = false;

      vm.removeQuery = removeQuery;
      vm.addBlankQuery = addBlankQuery;
      vm.loadJMXTree = loadJMXTree;
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

      function addBlankQuery() {
        if (vm.blankQuery && vm.blankQuery.obj) {
          vm.server.queries.push(vm.blankQuery);
        }

        vm.blankQuery = {
          obj: null,
          attr: [],
          typeNames: [],
          outputWriters: []
        };

        writerService.get().then(function (writer) {
          vm.blankQuery.outputWriters.push(writer);
        });
      }

      function loadJMXTree() {
        if (vm.server.host && vm.server.port) {
          var req = {
            method: 'GET',
            url: 'autocomplete',
            params: {
              host: vm.server.host,
              port: vm.server.port,
              username: vm.server.username,
              password: vm.server.password
            }
          };

          req.url = configService.getUrl() + req.url;

          $http(req)
            .success(function (response) {
              if (response.success) {
                vm.validJMXHost = true;
              } else {
                vm.validJMXHost = false;
              }
            })
            .error(function () {
              vm.validJMXHost = false;
            });
        }
      }

      function save() {
        vm.errorMessage = {};

        if (vm.blankQuery && vm.blankQuery.obj) {
          vm.server.queries.push(vm.blankQuery);
          vm.blankQuery = null;
        }

        angular.forEach(vm.blankAttr, function (attr, i) {
          if (attr && attr.value) {
            if (!vm.server.queries[i].attr) {
              vm.server.queries[i].attr = [];
            }
            vm.server.queries[i].attr.push(attr.value);
            vm.blankAttr[i] = null;
          }
        });

        angular.forEach(vm.blankTypeNames, function (typeName, i) {
          if (typeName && typeName.value) {
            if (!vm.server.queries[i].typeNames) {
              vm.server.queries[i].typeNames = [];
            }
            vm.server.queries[i].typeNames.push(typeName.value);
            vm.blankTypeNames[i] = null;
          }
        });

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
            angular.forEach(response, function (message) {
              vm.errorMessage[message.field] = message.message;
            });
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
            angular.forEach(response, function (message) {
              vm.errorMessage[message.field] = message.message;
            });
            ngToast.create({
              className: 'danger',
              content: 'An error occurred when saving server conf document'
            });
          });
        }
      }

      function showErrorMessage(key) {
        return key in vm.errorMessage;
      }
    };

    return Server;
  }
})();

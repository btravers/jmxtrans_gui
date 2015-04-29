(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .factory('serverFactory', serverFactory);

  function serverFactory($rootScope, $http, writerService, configService, ngToast) {

    var Server = function () {
      var vm = this;

      vm.id = null;
      vm.server = {
        port: null,
        host: null,
        queries: []
      };
      vm.saved = false;
      vm.blankQuery = null;
      vm.blankAttr = [];
      vm.blankTypeNames = [];
      vm.errorMessage = {};

      vm.removeQuery = removeQuery;
      vm.addBlankQuery = addBlankQuery;
      vm.loadJMXTree = loadJMXTree;
      vm.save = save;
      vm.showErrorMessage = showErrorMessage;


      function removeQuery(index) {
        vm.server.queries.splice(index, 1);
        vm.saved = false;
      }

      function addBlankQuery() {
        if (vm.blankQuery && vm.blankQuery.obj) {
          vm.server.queries.push(vm.blankQuery);
        }

        vm.blankQuery = {
          obj: null,
          attr: [],
          typeNames: [],
          outputWriters: [
            writerService.get()
          ]
        };
      }

      function loadJMXTree() {
        if (vm.server.host && vm.server.port) {
          var req = {
            method: 'GET',
            url: 'refresh',
            params: {
              host: vm.server.host,
              port: vm.server.port
            }
          };

          req.url = configService.getUrl() + req.url;

          $http(req)
            .error(function () {
              ngToast.create({
                className: 'danger',
                content: 'An error occurred when retrieving JMX object names information'
              });
            });
        }
      }

      function save() {
        if (!vm.saved) {
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
            var req = {
              method: 'POST',
              url: 'server/_update',
              params: {
                id: vm.id
              },
              data: {
                servers: [vm.server]
              }
            };

            req.url = configService.getUrl() + req.url;

            $http(req)
              .success(function () {
                vm.saved = true;
                ngToast.create({
                  className: 'success',
                  content: 'Save server conf document successfully'
                });
                $rootScope.$emit('update', {
                  host: vm.server.host,
                  port: vm.server.port
                });
              })
              .error(function (response) {
                angular.forEach(response, function (message) {
                  vm.errorMessage[message.field] = message.message;
                });
                ngToast.create({
                  className: 'danger',
                  content: 'An error occurred when saving server conf document'
                });
              });
          } else {
            var req = {
              method: 'POST',
              url: 'server',
              data: {
                servers: [vm.server]
              }
            };

            req.url = configService.getUrl() + req.url;

            $http(req)
              .success(function () {
                ngToast.create({
                  className: 'success',
                  content: 'Save server conf document successfully'
                });
                $rootScope.$emit('update', {
                  host: vm.server.host,
                  port: vm.server.port
                });
              })
              .error(function (response) {
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
      }

      function showErrorMessage(key) {
        return key in vm.errorMessage;
      }
    };

    return Server;
  }
})();

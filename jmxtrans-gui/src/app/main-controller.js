(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .controller('Main', Main);

  function Main($rootScope, $scope, $http, $modal, FileUploader, serverService, serverFactory, writerService, configService, ngToast, SweetAlert) {

    $scope.server = null;
    $scope.list = [];

    $scope.printServer = printServer;
    $scope.updateList = updateList;
    $scope.createServer = createServer;
    $scope.displayServer = displayServer;
    $scope.downloadServer = downloadServer;
    $scope.duplicateServer = duplicateServer;
    $scope.deleteServer = deleteServer;
    $scope.unSavedChanges = unSavedChanges;
    $scope.deleteConfirmation = deleteConfirmation;
    $scope.openWriter = openWriter;
    $scope.openUploader = openUploader;

    init();

    $rootScope.$on('update', function (event, data) {
      $scope.updateList();
      if (data.host && data.port) {
        $scope.displayServer(data.host, data.port);
      }
    });

    $rootScope.$on('save', function () {
      $scope.jmxForm.$setPristine();
    });

    function init() {
      $scope.updateList();
      setInterval($scope.updateList, configService.getReloadInterval());

      writerService.get().then(function (writer) {
        $scope.writer = writer;
        if (!$scope.writer['@class']) {
          ngToast.create({
            className: 'warning',
            dismissOnTimeout: false,
            content: 'No writer set'
          });
        }
      }, function () {

      });
    }

    function printServer(host, port) {
      if (host.length < 16) {
        return host + ':' + port;
      } else {
        var res = '';

        for (var i=0; i<13; i++) {
          res += host[i];
        }

        res += '...:' + port;

        return res;
      }
    }

    function updateList() {
      serverService.findAllHostsAndPorts().then(function (list) {
        $scope.list = list;
      }, function () {
        ngToast.create({
          className: 'danger',
          content: 'An error occurred when loading servers list'
        });
      });
    }

    function createServer() {
      $scope.server = new serverFactory();
      $scope.server.currentForm = $scope.jmxForm;
    }

    function displayServer(host, port) {
      serverService.getServer(host, port).then(function (server) {
        $scope.server = new serverFactory();
        $scope.server.server = server.server;
        $scope.server.id = server.id;
        $scope.server.saved = true;
        $scope.server.loadJMXTree();
        $scope.server.currentForm = $scope.jmxForm;
      }, function () {
        ngToast.create({
          className: 'danger',
          content: 'An error occurred when loading server conf document'
        });
      });
    }

    function downloadServer(host, port) {
      var req = {
        url: configService.getUrl() + 'server/_download',
        method: 'GET',
        params: {
          host: host,
          port: port
        },
        headers: {
          'Content-type': 'application/json'
        },
        responseType: 'arraybuffer'
      };

      $http(req)
        .success(function (data) {
          var file = new Blob([data], {
            type: 'application/json'
          });

          var fileURL = URL.createObjectURL(file);
          var serverFile = document.createElement('a');
          serverFile.href = fileURL;
          serverFile.target = '_blank';
          serverFile.download = host + ':' + port + '.json';
          document.body.appendChild(serverFile);
          serverFile.click();
        })
        .error(function (data, status, headers, config) {

        });
    }

    function duplicateServer(host, port) {
      serverService.getServer(host, port).then(function (server) {
        $scope.server = new serverFactory();
        $scope.server.server = server.server;
        $scope.server.server.host = null;
        $scope.server.server.port = null;
        $scope.server.server.username = null;
        $scope.server.server.password = null;
        $scope.server.currentForm = $scope.jmxForm;
      }, function () {
        ngToast.create({
          className: 'danger',
          content: 'An error occurred when loading server conf document'
        });
      });
    }

    function deleteServer(host, port) {
      serverService.deleteServer(host, port).then(function () {
        ngToast.create({
          className: 'success',
          content: "Delete server conf document successfully"
        });
        $scope.server = null;
        $scope.updateList();
      }, function () {
        ngToast.create({
          className: 'danger',
          content: 'An error occurred during the deletion of the server conf document'
        });
      });
    }

    function unSavedChanges(performAction) {
      var args = arguments;
      if ($scope.jmxForm.$dirty) {
        SweetAlert.swal({
            title: 'Some changes have not been saved!',
            text: 'Changes will be lost if you continue.',
            type: 'warning',
            showCancelButton: true,
            confirmButtonText: "Continue"
          },
          function (isConfirm) {
            if (isConfirm) {
              performAction.apply(this, Array.prototype.slice.call(args, 1));
              $scope.jmxForm.$setPristine();
            }
          });
      } else {
        performAction.apply(this, Array.prototype.slice.call(args, 1));
      }
    }

    function deleteConfirmation(host, port) {
      var close = true;
      if ($scope.jmxForm.$dirty) {
        close = false;
      }
      SweetAlert.swal({
        title: 'Are you sure?',
        text: 'You will not be able to recover the server conf document after deleting.',
        type: 'warning',
        showCancelButton: true,
        confirmButtonText: "Delete",
        closeOnConfirm: close
      }, function (isConfirm) {
        if (isConfirm) {
          unSavedChanges(deleteServer, host, port);
        }
      });
    }

    /**
     * Writer modal
     */
    function openWriter() {
      var modal = $modal.open({
        templateUrl: 'app/components/writer/writer.html',
        controller: function ($scope, $modalInstance, writerService) {

          $scope.writer = null;

          writerService.get().then(function (writer) {
            $scope.writer = writer;
          }, function () {
            $scope.writer = {
              '@class': null,
              settings: {}
            };
          });

          $scope.save = function () {
            $modalInstance.close($scope.writer);
          };

          $scope.cancel = function () {
            $modalInstance.dismiss();
          };

          $scope.writerChange = function () {
            switch ($scope.writer['@class']) {
              case 'com.googlecode.jmxtrans.model.output.BluefloodWriter':
                $scope.writer.settings.port = 19000;
                break;
              case 'com.googlecode.jmxtrans.model.output.DailyKeyOutWriterForm':
                break;
              case 'com.googlecode.jmxtrans.model.output.Ganglia':
                break;
              case 'com.googlecode.jmxtrans.model.output.Graphite':
                $scope.writer.settings.port = 2003;
                break;
              case 'com.googlecode.jmxtrans.model.output.RRDToolWriter':
                break;
              case 'com.googlecode.jmxtrans.model.output.StatsDWriter':
                break;
            }
          };

        }
      });

      modal.result.then(function (writer) {
        writerService.set(writer);
      }, function () {

      });
    }

    /**
     * Uploader modal
     */
    function openUploader() {
      var modal = $modal.open({
        templateUrl: 'app/components/uploader/uploader.html',
        controller: function ($scope, $modalInstance, writerService, configService, updateList) {

          var url = configService.getUrl() + 'upload';

          $scope.uploader = new FileUploader({
            url: url
          });

          $scope.uploader.onCompleteAll = function () {
            writerService.get();
            updateList();
          };

          $scope.cancel = function () {
            $modalInstance.dismiss();
          };

        }, resolve: {
          updateList: function () {
            return $scope.updateList;
          }
        }
      });

      modal.result.then(function () {

      }, function () {

      });
    }
  }
})
();

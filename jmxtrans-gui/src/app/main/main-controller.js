(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .controller('Main', Main);

  function Main($rootScope, $scope, $http, $modal, FileUploader, serverService, serverFactory, writerService, configService, ngToast, SweetAlert) {

    $scope.server = null;
    $scope.blankServer = null;
    $scope.list = [];

    $scope.updateServersList = updateServersList;
    $scope.addBlankServer = addBlankServer;
    $scope.cancelBlankServer = cancelBlankServer;
    $scope.display = display;
    $scope.download = download;
    $scope.duplicate = duplicate;
    $scope.delete = deleteServer;
    $scope.unSavedChanges = unSavedChanges;
    $scope.deleteConfirmation = deleteConfirmation;
    $scope.openWriter = openWriter;
    $scope.openUploader = openUploader;


    init();

    $rootScope.$on('update', function (event, data) {
      $scope.updateServersList();
      if (data.host && data.port) {
        $scope.display(data.host, data.port);
      }
    });

    function init() {
      $scope.updateServersList();
      setInterval($scope.updateServersList, configService.getReloadInterval());

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

    function updateServersList() {
      serverService.findAllHostsAndPorts().then(function (list) {
        $scope.list = list;
      });
    }

    function addBlankServer() {
      $scope.blankServer = new serverFactory();
      $scope.server = null;
    }

    function cancelBlankServer() {
      $scope.blankServer = null;
    }

    function display(host, port) {
      serverService.getServer(host, port).then(function (server) {
        $scope.server = new serverFactory();
        $scope.server.server = server.server;
        $scope.server.id = server.id;
        $scope.server.saved = true;
        $scope.server.loadJMXTree();

        $scope.blankServer = null;
      });
    }

    function download(host, port) {
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


    function duplicate(host, port) {
      serverService.getServer(host, port).then(function (server) {
        $scope.blankServer = new serverFactory();
        $scope.blankServer.server = server.server;
        $scope.blankServer.server.host = null;
        $scope.blankServer.server.port = null;

        $scope.server = null;
      });
    }

    function deleteServer(host, port) {
      serverService.deleteServer(host, port).then(function () {
        $scope.server = null;
        $scope.updateServersList();
      });
    }

    function unSavedChanges(performAction) {
      var args = arguments;
      if ($scope.blankServer || ($scope.server && !$scope.server.saved)) {
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
            }
          });
      } else {
        performAction.apply(this, Array.prototype.slice.call(args, 1));
      }
    }

    function deleteConfirmation(host, port) {
      var close = true;
      if ($scope.server && !$scope.server.saved) {
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
          unSavedChangesBeforeDelete(host, port);
        }
      });
    }

    function unSavedChangesBeforeDelete(host, port) {
      if ($scope.server && !$scope.server.saved) {
        SweetAlert.swal({
            title: 'Some changes have not been saved!',
            text: 'Changes will be lost if you continue.',
            type: 'warning',
            showCancelButton: true,
            confirmButtonText: "Continue"
          },
          function (isConfirm) {
            if (isConfirm) {
              $scope.delete(host, port);
            }
          });
      } else {
        $scope.delete(host, port);
      }
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
        controller: function ($scope, $modalInstance, writerService, configService, updateServersList) {

          var url = configService.getUrl() + 'upload';

          $scope.uploader = new FileUploader({
            url: url
          });

          $scope.uploader.onCompleteAll = function () {
            writerService.get();
            updateServersList();
          };

          $scope.cancel = function () {
            $modalInstance.dismiss();
          };

        }, resolve: {
          updateServersList: function () {
            return $scope.updateServersList;
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

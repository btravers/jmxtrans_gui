(function () {
  'use strict';

  angular
    .module('jmxtransGui')
    .controller('Main', Main);

  function Main($rootScope, $scope, $http, $q, $modal, FileUploader, serverFactory, writerService, configService, ngToast) {

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
    $scope.openWriter = openWriter;
    $scope.openUploader = openUploader;


    init();

    $rootScope.$on('update', function (event, data) {
      setTimeout(function () {
        $scope.updateServersList();
        if (data.host && data.port) {
          $scope.display(data.host, data.port);
        }
      }, 1000);
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
      var req = {
        method: 'GET',
        url: 'server/all'
      };

      req.url = configService.getUrl() + req.url;

      $http(req)
        .success(function (response) {
          $scope.list = response;
        })
        .error(function () {
          ngToast.create({
            className: 'danger',
            content: 'An error occurred when loading servers list'
          });
        });
    }

    function addBlankServer() {
      $scope.blankServer = new serverFactory();
      $scope.server = null;
    }

    function cancelBlankServer() {
      $scope.blankServer = null;
    }

    function getServer(host, port) {
      var req = {
        method: 'GET',
        url: 'server',
        params: {
          host: host,
          port: port
        }
      };

      req.url = configService.getUrl() + req.url;

      var server = $q.defer();

      $http(req)
        .success(function (response) {
          server.resolve({
            id: response.id,
            server: response.source.servers[0]
          });
        })
        .error(function () {
          ngToast.create({
            className: 'danger',
            content: 'An error occurred when loading server conf document'
          });
        }
      );

      return server.promise;
    }

    function display(host, port) {
      getServer(host, port).then(function (server) {
        $scope.server = new serverFactory();
        $scope.server.server = server.server;
        $scope.server.id = server.id;
        $scope.server.saved = true;
        $scope.server.loadJMXTree();

        $scope.blankServer = null;
      });
    }

    function download(host, port) {
      //return configService.getUrl() + 'server/_download?host=' + host + '&port=' + port;

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
      getServer(host, port).then(function (server) {
        $scope.blankServer = new serverFactory();
        $scope.blankServer.server = server.server;
        $scope.blankServer.server.host = null;
        $scope.blankServer.server.port = null;

        $scope.server = null;
      });
    }

    function deleteServer(host, port) {
      var req = {
        method: 'DELETE',
        url: 'server',
        params: {
          host: host,
          port: port
        }
      };

      req.url = configService.getUrl() + req.url;

      $http(req)
        .success(function () {
          // TODO
          $scope.server = null;
          ngToast.create({
            className: 'success',
            content: "Delete server conf document successfully"
          });

          setTimeout(function () {
            $scope.updateServersList();
          }, 1000);
        })
        .error(function () {
          ngToast.create({
            className: 'danger',
            content: 'An error occurred during the deletion of the server conf document'
          });
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
        $scope.writer = writer;
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
            setTimeout(function () {
              writerService.get();
              updateServersList();
            }, 1000);
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
})();

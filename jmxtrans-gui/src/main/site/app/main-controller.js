'use strict';

var app = angular.module('jmxtransGui');

app.controller('Main', function ($http, $scope, $q, $modal, FileUploader, serverFactory, writerService, configService) {

  $scope.$on('update', function (event, data) {
    $scope.updateServersList();
    $scope.display(data.home, data.post);
  });

  /**
   * Load writer in order to know if one is already set. Otherwise, print an alert message.
   */
  writerService.get().then(function (writer) {
    $scope.writer = writer;
  });

  $scope.server = null;
  $scope.blankServer = null;

  $scope.list = [];

  $scope.updateServersList = function () {
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

      });
  };

  $scope.updateServersList();

  $scope.addBlankServer = function () {
    $scope.blankServer = new serverFactory();
    $scope.server = null;
  };

  $scope.cancelBlankServer = function () {
    $scope.blankServer = null;
  };

  var get = function (host, port) {
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
      .error();

    return server.promise;
  };

  $scope.display = function (host, port) {
    get(host, port).then(function (server) {
      $scope.server = new serverFactory();
      $scope.server.server = server.server;
      $scope.server.id = server.id;
      $scope.server.saved = true;
      $scope.server.loadJMXTree();

      $scope.blankServer = null;
    });
  };

  $scope.download = function (host, port) {
    return configService.getUrl() + 'server/_download?host=' + host + '&port=' + port;
  };

  $scope.duplicate = function (host, port) {
    get(host, port).then(function (server) {
      $scope.blankServer = new serverFactory();
      $scope.blankServer.server = server.server;
      $scope.blankServer.server.host = null;
      $scope.blankServer.server.port = null;

      $scope.server = null;
    });
  };

  $scope.delete = function (host, port) {
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

        setTimeout(function () {
          $scope.updateServersList();
        }, 1000);
      })
      .error(function () {

      });
  };


  /**
   * Writer modal
   */
  $scope.openWriter = function () {
    var modal = $modal.open({
      templateUrl: 'app/components/writer/writer.html',
      controller: function ($scope, $modalInstance, writerService) {

        $scope.writer = null;

        writerService.get().then(function (writer) {
          $scope.writer = writer;
        }, function () {

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
  };


  /**
   * Uploader modal
   */
  $scope.openUploader = function () {
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
        updateServersList: function() {
          return $scope.updateServersList;
        }
      }
    });

    modal.result.then(function () {

    }, function () {

    });
  };

});

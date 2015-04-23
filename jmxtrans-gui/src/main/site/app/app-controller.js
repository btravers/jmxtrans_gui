'use strict';

var app = angular.module('jmxtransGui');

app.controller('Main', function ($scope, $http, $modal, FileUploader, serverFactory, serverService, writerService) {

  $scope.writer = writerService.get();
  serverService.updateServersList();
  $scope.list = serverService.getList();

  $scope.server = null;

  $scope.display = function (host, port) {
    var server = serverService.get(host, port);
    $scope.server = serverFactory;
    $scope.server.server = server.server;
    $scope.server.id = server.id;
    $scope.server.saved = true;
  };

  $scope.delete = function (host, port) {
    serverService.delete(host, port);
  }

  $scope.blankServer = null;

  $scope.duplicate = function (host, port) {
    var server = serverService.get(host, port);
    $scope.blankServer = serverFactory;
    $scope.blankServer.server = server.server;
    $scope.blankServer.server.host = null;
    $scope.blankServer.server.id = null;
  };

  $scope.addBlankServer = function () {
    $scope.blankServer = serverFactory;
  };

  $scope.cancelBlankServer = function () {
    $scope.blankServer = null;
  };


  /**
   * Writer modal
   */
  $scope.openWriter = function () {
    var modal = $modal.open({
      templateUrl: 'app/components/writer/writer.html',
      controller: function ($scope, $modalInstance, writerService) {

        $scope.writer = writerService.get();

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
      controller: function ($scope, $modalInstance, writerService, serverService) {

        $scope.uploader = new FileUploader({
          url: 'upload'
        });

        $scope.uploader.onCompleteAll = function () {
          setTimeout(function () {
            writerService.get();
            serverService.updateServersList();
          }, 1000);
        };

        $scope.cancel = function () {
          $modalInstance.dismiss();
        };

      }
    });

    modal.result.then(function () {

    }, function () {

    });
  };

});

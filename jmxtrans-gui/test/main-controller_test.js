'use strict';

describe('Main', function () {

  var $controller;

  beforeEach(module('jmxtransGui'));

  beforeEach(inject(function(_$controller_) {
    $controller = _$controller_;
  }));

  describe('$scope.addBlankServer', function() {
    it('should create a blank server', function() {
      var $scope = {};
      $controller('Main', { $scope: $scope });

      $scope.addBlankServer();
      expect($scope.blankServer).not.toBeNull();
    });
  });

});

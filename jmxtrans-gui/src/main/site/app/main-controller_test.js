describe('Main', function () {

  beforeEach(module('jmxtransGui'));

  var $controller;

  beforeEach(inject(function(_$controller_) {
    $controller = _$controller_;
  }));

  describe('$scope.addBlankServer', function() {
    it('should create a blank server', function() {
      var $scope = {};
      var controller = $controller('Main', { $scope: $scope });

      $scope.addBlankServer();

      expect($scope.server).toBeNull();
      expect($scope.blankServer).not.toBeNull();
    });
  });

  describe('$scope.cancelBlankServer', function() {
    it('should set the blank server to null', function() {
      var $scope = {};
      var controller = $controller('Main', { $scope: $scope });

      $scope.addBlankServer();

      expect($scope.server).toBeNull();
      expect($scope.blankServer).not.toBeNull();

      $scope.cancelBlankServer();

      expect($scope.server).toBeNull();
      expect($scope.blankServer).toBeNull();
    });
  });
});

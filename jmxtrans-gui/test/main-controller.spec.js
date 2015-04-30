'use strict';

describe('Main Controller Test', function () {

  var serverServiceMock;
  var writerServiceMock;
  var serverFactoryMock;

  beforeEach(module('jmxtransGui'));

  beforeEach(inject(function ($q) {
    serverServiceMock = {
      findAllHostsAndPorts: function () {
        var list = $q.defer();
        list.resolve([
          {
            host: '192.168.0.1',
            port: 9991
          },
          {
            host: '192.168.0.2',
            port: 9991
          },
          {
            host: '192.168.0.3',
            port: 9991
          }
        ]);
        return list.promise;
      },
      getServer: function () {
        var server = $q.defer();
        server.resolve({
          id: '1',
          server: {
            port: "9991",
            host: "192.168.0.1",
            queries: [
              {
                obj: "java.lang:type=Memory",
                attr: [
                  "HeapMemoryUsage",
                  "NonHeapMemoryUsage"
                ],
                outputWriters: [
                  {
                    '@class': "com.googlecode.jmxtrans.model.output.BluefloodWriter",
                    settings: {
                      port: 19000,
                      host: "localhost"
                    }
                  }
                ]
              }
            ]
          }
        });
        return server.promise;
      },
      deleteServer: function () {
        var promise = $q.defer();
        promise.resolve();
        return promise.promise;
      }
    };

    writerServiceMock = {
      get: function () {
        var writer = $q.defer();
        writer.resolve({
          '@class': "com.googlecode.jmxtrans.model.output.BluefloodWriter",
          settings: {
            port: 19000,
            host: "localhost"
          }
        });
        return writer.promise;
      },
      set: function () {

      }
    };

    serverFactoryMock = function () {
      this.loadJMXTree = function () {

      };
    };

  }));

  it('should create a blank server', inject(function ($rootScope, $controller) {
    var $scope = $rootScope.$new();

    $controller('Main', {
      $scope: $scope
    });

    $scope.addBlankServer();
    expect($scope.blankServer).not.toBeNull();
  }));

  it('should remove the blank server after creation', inject(function ($rootScope, $controller) {
    var $scope = $rootScope.$new();

    $controller('Main', {
      $scope: $scope
    });

    $scope.addBlankServer();
    expect($scope.blankServer).not.toBeNull();

    $scope.cancelBlankServer();
    expect($scope.blankServer).toBeNull();
  }));

  it('should update the list of servers', inject(function ($rootScope, $controller) {
    var $scope = $rootScope.$new();

    $controller('Main', {
      $scope: $scope,
      serverService: serverServiceMock,
      writerService: writerServiceMock
    });

    $scope.updateServersList();
    $rootScope.$apply();

    expect($scope.list.length).not.toEqual(0);
    expect($scope.list).toEqual(jasmine.arrayContaining([
      {
        host: '192.168.0.1',
        port: 9991
      },
      {
        host: '192.168.0.2',
        port: 9991
      },
      {
        host: '192.168.0.3',
        port: 9991
      }
    ]));
  }));

  it('should display wanted server', inject(function ($rootScope, $controller) {
    var $scope = $rootScope.$new();

    $controller('Main', {
      $scope: $scope,
      serverService: serverServiceMock,
      writerService: writerServiceMock,
      serverFactory: serverFactoryMock
    });

    $scope.display();
    $rootScope.$apply();

    expect($scope.server).not.toBe(null);
    expect($scope.blankServer).toBe(null);
    expect($scope.server.server).toEqual({
      port: "9991",
      host: "192.168.0.1",
      queries: [
        {
          obj: "java.lang:type=Memory",
          attr: [
            "HeapMemoryUsage",
            "NonHeapMemoryUsage"
          ],
          outputWriters: [
            {
              '@class': "com.googlecode.jmxtrans.model.output.BluefloodWriter",
              settings: {
                port: 19000,
                host: "localhost"
              }
            }
          ]
        }
      ]
    });
  }));

  it('should duplicate wanted server', inject(function ($rootScope, $controller) {
    var $scope = $rootScope.$new();

    $controller('Main', {
      $scope: $scope,
      serverService: serverServiceMock,
      writerService: writerServiceMock,
      serverFactory: serverFactoryMock
    });

    $scope.duplicate();
    $rootScope.$apply();

    expect($scope.blankServer).not.toBe(null);
    expect($scope.server).toBe(null);
    expect($scope.blankServer.server).toEqual({
      port: null,
      host: null,
      queries: [
        {
          obj: "java.lang:type=Memory",
          attr: [
            "HeapMemoryUsage",
            "NonHeapMemoryUsage"
          ],
          outputWriters: [
            {
              '@class': "com.googlecode.jmxtrans.model.output.BluefloodWriter",
              settings: {
                port: 19000,
                host: "localhost"
              }
            }
          ]
        }
      ]
    });
  }));

  it('should delete the server', inject(function ($rootScope, $controller) {
    var $scope = $rootScope.$new();

    $controller('Main', {
      $scope: $scope,
      serverService: serverServiceMock,
      writerService: writerServiceMock,
      serverFactory: serverFactoryMock
    });

    spyOn(serverServiceMock, 'deleteServer').and.callThrough();
    spyOn($scope, 'updateServersList').and.callThrough();

    $scope.delete();
    $rootScope.$apply();

    expect(serverServiceMock.deleteServer).toHaveBeenCalled();
    expect($scope.updateServersList).toHaveBeenCalled();
  }));

});

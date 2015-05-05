'use strict';

describe('Config Service Test', function () {

  beforeEach(function() {
    module(function($provide) {
      $provide.service('$location', function() {
        this.port = jasmine.createSpy('port');
      });
    });

    module('jmxtransGui');
  });

  it('Should get url', inject(function(configService, $location) {
    expect(configService.getUrl()).toEqual('');
    expect($location.port).toHaveBeenCalled();
  }));

  it('Should get reload interval', inject(function(configService) {
    expect(configService.getReloadInterval()).toEqual(60000);
  }));

});

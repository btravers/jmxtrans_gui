'use strict';

describe('Writer Service Test', function () {

  var $httpBackend, $q;

  beforeEach(module('jmxtransGui'));

  beforeEach(inject(function ($injector) {
    $httpBackend = $injector.get('$httpBackend');

    $httpBackend.when('GET', 'settings').respond({
      '@class': 'com.googlecode.jmxtrans.model.output.BluefloodWriter',
      settings: {
        port: 19000,
        host: 'localhost'
      }
    });

    $httpBackend.when('POST', 'settings').respond();
  }));

  it('Should get the writer', inject(function (writerService) {
    $httpBackend.expect('GET', 'settings');

    var writer;
    writerService.get().then(function (data) {
      writer = data;
    });
    $httpBackend.flush();

    expect(writer).toEqual({
      '@class': 'com.googlecode.jmxtrans.model.output.BluefloodWriter',
      settings: {
        port: 19000,
        host: 'localhost'
      }
    });
  }));

  it('Should set the writer', inject(function (writerService) {
    $httpBackend.expect('POST', 'settings');

    writerService.set(
      {
        '@class': 'com.googlecode.jmxtrans.model.output.BluefloodWriter',
        settings: {
          port: 19000,
          host: 'localhost'
        }
      }
    );

    $httpBackend.flush();
  }));

});

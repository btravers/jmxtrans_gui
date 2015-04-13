var app = angular.module('Jmxtrans', ['angularFileUpload']);

app.controller('Main', function($scope, $http, FileUploader) {
	
	$scope.list = [];

	$scope.uploader = new FileUploader({ 
		url: 'service/upload' 
	});

	$http.get('service/settings').success(function(data) {
		$scope.writer = data;
	});

	if (window.File && window.FileReader && window.FileList && window.Blob) {
		$scope.fileSupport = true;	
	} else {
		$scope.fileSupport = false;	
	}

	var req = {
		method: 'GET',
		url: 'service/server/all'
	};

	$http(req)
		.success(function(response) {
			$scope.list = response;
		})
		.error(console.err);

	$scope.writerChange = function() {
		switch($scope.writer.writer) {
			case 'com.googlecode.jmxtrans.model.output.BluefloodWriter':
				$scope.writer.port = 19000;
				break;
			case 'com.googlecode.jmxtrans.model.output.Graphite':
				$scope.writer.port = 2003;
				break;
		}
	};

	$scope.saveWriter = function() {
		var req = {
			method: 'POST',
			url: 'service/settings',
			data: $scope.writer
		}

		$http(req)
			.success(function(response) {

			})
			.error(console.err);
	};

	$scope.delete = function(item) {
		var req = {
			method: 'DELETE',
			url: 'service/server/_host',
			params: {
				host: item
			}
		};

		$http(req)
			.success(function(response) {
				setTimeout(function() {
					window.location.reload();
				}, 500);
			})
			.error(console.err);
	};

	$scope.duplicate = function(item) {
		var req = {
			method: 'GET',
			url: 'service/server',
			params: {
				host: item
			}
		};

		$http(req)
			.success(function(response) {
				$scope.blankServer = {
					saved: false,
					blankAttr: [],
					server: response.source.servers[0],
					id: null
				};
				$scope.blankServer.server.host = null;
				$scope.blankServer.server.port = null;
			})
			.error(console.err);
	};

	$scope.display = function(item) {
		var req = {
			method: 'GET',
			url: 'service/server',
			params: {
				host: item
			}
		};

		$http(req)
			.success(function(response) {
				var server = {
					saved: true,
					blankAttr: [],
					server: response.source.servers[0],
					id: response.id
				};
				$scope.servers = [server];

				$scope.blankServer = null;
			})
			.error(console.err);
	};

	$scope.addBlankServer = function() {
		$scope.blankServer = {
			saved: false,
			blankAttr: [],
			server: {
				port: null,
				host: null,
				queries: []
			}
		};
	};

	$scope.cancelBlankServer = function() {
		$scope.blankServer = null;
	};

	$scope.removeServer = function(index) {
		var req = {
			method: 'DELETE',
			url: 'service/server',
			params: {
				id: $scope.servers[index].id
			}
		};

		$http(req)
			.success(function(response) {
				$scope.servers.splice(index, 1);

				if ($scope.servers.length == 0) {
					setTimeout(function() {
						window.location.reload();
					}, 500);
				}
			})
			.error(console.err);
	};

	$scope.duplicateServer = function(server) {
		$scope.blankServer = angular.copy(server);

		$scope.blankServer.id = null;
		$scope.blankServer.saved = false; 
	};
});

app.directive('server', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			server: '=server',
			index: '=index',
			writer: '=writer'
		},
		controller: function($scope, $http) {

			$scope.addBlankQuery = function() {
				if ($scope.server.blankQuery && $scope.server.blankQuery.obj) {
					$scope.server.server.queries.push($scope.server.blankQuery);
				}

				$scope.server.blankQuery = {
					obj: null,
					attr: [],
					outputWriters: [
						{
							'@class': $scope.writer.class, 
							settings: {
								port: $scope.writer.port, 
								host: $scope.writer.host
							}
						}
					]
				};

				if ($scope.writer.username) {
					$scope.server.blankQuery.outputWriters.settings.username = $scope.writer.username;
				}

				if ($scope.writer.password) {
					$scope.server.blankQuery.outputWriters.settings.password = $scope.writer.password;
				}
			};

			$scope.saveServer = function() {
				if (!$scope.server.saved && $scope.server.server.host && $scope.server.server.port) {
					if ($scope.server.blankQuery && $scope.server.blankQuery.obj) {
						$scope.server.server.queries.push($scope.server.blankQuery);
						$scope.server.blankQuery = null;
					}

					if ($scope.server.blankAttr) {
						angular.forEach($scope.server.blankAttr, function(attr, i) {
							if (attr && attr.value) {
								$scope.server.queries[+i].attr.push(attr.value);
								$scope.server.blankAttr[i] = null;
							}
						});
					}

					if ($scope.server.id) {
						var req = {
							method: 'POST',
							url: 'service/server/_update',
							params: {
								id: $scope.server.id
							},
							data: {
								servers: [$scope.server.server]
							}
						};

						$http(req)
							.success(function(response) {
								$scope.server.saved = true;
								setTimeout(function() {
									window.location.reload();
								}, 500);
							})
							.error(console.error);
					} else {
						var req = {
							method: 'POST',
							url: 'service/server',
							data: { 
								servers: [$scope.server.server]
							}
						};

						$http(req)
							.success(function(response) {
								$scope.server = null;
								setTimeout(function() {
									window.location.reload();
								}, 500);
							})
							.error(console.error);
					}
				}
			};

			$scope.unsaved = function() {
				$scope.server.saved = false;
			};
		},
		templateUrl: 'server.html'
	};
});

app.directive('query', function() {
	return {
		require: 'server',
		restrict: 'E',
		replace: true,
		scope: {
			query: '=query',
			server: '=server',
			queryIndex: '=queryIndex',
			serverIndex: '=serverIndex'
		},
		controller: function($scope) {

			$scope.unsaved = function() {
				$scope.server.saved = false;
			};

			$scope.addBlankAttr = function() {
				if ($scope.server.blankAttr[$scope.queryIndex] && $scope.server.blankAttr[$scope.queryIndex].value) {
					console.log('Push the old value');
					$scope.query.attr.push($scope.server.blankAttr[$scope.queryIndex].value);
				}

				$scope.server.blankAttr[$scope.queryIndex] = {
					value: null
				};
			};

			$scope.removeAttr = function(index) {
				$scope.query.attr.splice(index, 1);
				$scope.server.saved = false;
			};

			$scope.removeBlankAttr = function() {
				$scope.server.blankAttr[$scope.queryIndex] = null;
			};

			$scope.removeQuery = function() {
				if ($scope.queryIndex < $scope.server.server.queries.length) {
					$scope.server.server.queries.splice($scope.queryIndex, 1);
					$scope.server.saved = false;
				}
				$scope.query = null;
			}

		},
		templateUrl: 'query.html'
	};
});
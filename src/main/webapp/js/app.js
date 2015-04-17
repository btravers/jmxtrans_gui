var app = angular.module('Jmxtrans', ['angularFileUpload', 'ui.bootstrap']);

app.controller('Main', function($scope, $http, FileUploader) {
	
	$scope.list = [];

	$scope.errorMessage = {};

	$scope.uploader = new FileUploader({ 
		url: 'service/upload' 
	});

	$scope.uploader.onCompleteAll = function() {
		setTimeout(function() {
			var req = {
				method: 'GET',
				url: 'service/server/all'
			};

			$http(req)
				.success(function(response) {
					$scope.list = response;
				})
				.error(console.log);
		}, 1000);
	};

	$http.get('service/settings').success(function(data) {
		$scope.writer = data;
		if (!$scope.writer.settings) {
			$scope.writer.settings = {};
		}
	});

	$scope.updateServersList = function() {
		var req = {
			method: 'GET',
			url: 'service/server/all'
		};

		$http(req)
			.success(function(response) {
				$scope.list = response;

				if ($scope.blankServer) {
					$scope.display($scope.blankServer.server.host);
				} else if ($scope.servers && $scope.servers.length > 0) {
					$scope.display($scope.servers[0].server.host);
				}
			})
			.error(console.log);
	};

	$scope.updateServersList();

	$scope.writerChange = function() {
		switch($scope.writer['@class']) {
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
			.error(function(response) {
				angular.forEach(response, function(message) {
					$scope.errorMessage[message.field] = message.message;
				});
			});
	};

	$scope.delete = function(item) {
		var req = {
			method: 'DELETE',
			url: 'service/server',
			params: {
				host: item
			}
		};

		$http(req)
			.success(function(response) {
				setTimeout(function() {
					$scope.updateServersList();
				}, 1000);
			})
			.error(console.log);
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
					blankTypeNames: [],
					server: response.source.servers[0],
					id: null
				};
				$scope.blankServer.server.host = null;
				$scope.blankServer.server.port = null;
			})
			.error(console.log);
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
				if (response.source) {
					var server = {
						saved: true,
						blankAttr: [],
						blankTypeNames: [],
						server: response.source.servers[0],
						id: response.id
					};

					$scope.servers = [server];
				} else {
					$scope.servers = [];
				}
				

				$scope.blankServer = null;
			})
			.error(console.log);
	};

	$scope.addBlankServer = function() {
		$scope.blankServer = {
			saved: false,
			blankAttr: [],
			blankTypeNames: [],
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
});

app.directive('server', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			server: '=server',
			index: '=index',
			writer: '=writer',
			updateServersList: '&updateServersList'
		},
		controller: function($scope, $http) {

			$scope.update = $scope.updateServersList();

			$scope.errorMessage = {};

			$scope.showErrorMessage = function(index, field) {
				var s = 'servers[' + index + '].' + field;

				return typeof $scope.errorMessage[s] != "undefined" && $scope.errorMessage[s] != null;
			};

			$scope.loadJMXTree = function() {
				var req = {
					method: 'GET',
					url: 'service/refresh',
					params: {
						host: $scope.server.server.host,
						port: $scope.server.server.port
					}
				};

				$http(req);			
			};

			$scope.addBlankQuery = function() {
				if ($scope.server.blankQuery && $scope.server.blankQuery.obj) {
					if (!$scope.server.queries) {
						$scope.server.queries = [];
					}
					$scope.server.server.queries.push($scope.server.blankQuery);
				}

				$scope.server.blankQuery = {
					obj: null,
					attr: [],
					typeNames: [],
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
				if (!$scope.server.saved) {
					if ($scope.server.blankQuery && $scope.server.blankQuery.obj) {
						if (!$scope.server.queries) {
							$scope.server.queries = [];
						}
						$scope.server.server.queries.push($scope.server.blankQuery);
						$scope.server.blankQuery = null;
					}

					if ($scope.server.blankAttr) {
						angular.forEach($scope.server.blankAttr, function(attr, i) {
							if (attr && attr.value) {
								if (!$scope.server.server.queries[i].attr) {
									$scope.server.server.queries[i].attr = [];
								}
								$scope.server.server.queries[i].attr.push(attr.value);
								$scope.server.blankAttr[i] = null;
							}
						});
					}

					if ($scope.server.blankTypeNames) {
						angular.forEach($scope.server.blankTypeNames, function(typeName, i) {
							if (typeName && typeName.value) {
								if (!$scope.server.server.queries[i].typeNames) {
									$scope.server.server.queries[i].typeNames = [];
								}
								$scope.server.server.queries[i].typeNames.push(typeName.value);
								$scope.server.blankTypeNames[i] = null;
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
									$scope.update();
								}, 1000);
							})
							.error(function(response) {
								angular.forEach(response, function(message) {
									$scope.errorMessage[message.field] = message.message;
								});
							});
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
									$scope.update();
								}, 1000);
							})
							.error(function(response) {
								angular.forEach(response, function(message) {
									$scope.errorMessage[message.field] = message.message;
								});
							});
					}
				}
			};

			$scope.unsaved = function() {
				$scope.server.saved = false;
			};
		},
		templateUrl: 'template/server.html'
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
			serverIndex: '=serverIndex',
			errorMessage: "=errorMessage"
		},
		controller: function($scope, $http) {

			$scope.showErrorMessage = function(serverIndex, queryIndex, field) {
				var s = 'servers[' + serverIndex + '].queries[' + queryIndex + '].' + field;

				return typeof $scope.errorMessage[s] != "undefined" && $scope.errorMessage[s] != null;
			};

			$scope.suggestName = function(name) {
				if (!$scope.nameSuggestions) {
					var req = {
						method: 'GET',
						url: 'service/suggest_name',
						params: {
							host: $scope.server.server.host
						}
					};

					$http(req)
						.success(function(response) {
							$scope.nameSuggestions = response;
						})
						.error(console.log);
				}
			};

			$scope.suggestAttr = function(attr) {
				if (!$scope.attrSuggestions) {
					var req = {
						method: 'GET',
						url: 'service/suggest_attr',
						params: {
							host: $scope.server.server.host,
							name: $scope.query.obj
						}
					};

					$http(req)
						.success(function(response) {
							$scope.attrSuggestions = response;
						})
						.error(console.log);
				}
			};

			$scope.unsaved = function() {
				$scope.server.saved = false;
			};

			$scope.addBlankAttr = function() {
				if ($scope.server.blankAttr[$scope.queryIndex] && $scope.server.blankAttr[$scope.queryIndex].value) {
					if (!$scope.query.attr) {
						$scope.query.attr = [];
					}
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

			$scope.addBlankTypeName = function() {
				if ($scope.server.blankTypeNames[$scope.queryIndex] && $scope.server.blankTypeNames[$scope.queryIndex].value) {
					if (!$scope.query.typeNames) {
						$scope.query.typeNames = [];
					}
					$scope.query.typeNames.push($scope.server.blankTypeNames[$scope.queryIndex].value);
				}

				$scope.server.blankTypeNames[$scope.queryIndex] = {
					value: null
				};
			};

			$scope.removeTypeName = function(index) {
				$scope.query.typeNames.splice(index, 1);
				$scope.server.saved = false;
			};

			$scope.removeBlankTypeName = function() {
				$scope.server.blankTypeNames[$scope.queryIndex] = null;
			};

			$scope.removeQuery = function() {
				if ($scope.queryIndex < $scope.server.server.queries.length) {
					$scope.server.server.queries.splice($scope.queryIndex, 1);
					$scope.server.saved = false;
				}
				$scope.query = null;
			}

		},
		templateUrl: 'template/query.html'
	};
});

app.directive('bluefloodWriterForm', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			writer: '=writer'
		},
		templateUrl: 'template/bluefloodWriterForm.html'
	};
});


app.directive('dailyKeyOutWriterForm', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			writer: '=writer'
		},
		templateUrl: 'template/dailyKeyOutWriterForm.html'
	};
});

app.directive('gangliaWriterForm', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			writer: '=writer'
		},
		templateUrl: 'template/gangliaWriterForm.html'
	};
});

app.directive('graphiteWriterForm', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			writer: '=writer'
		},
		templateUrl: 'template/graphiteWriterForm.html'
	};
});
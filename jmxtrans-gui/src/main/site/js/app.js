var app = angular.module('Jmxtrans', ['angularFileUpload', 'ui.bootstrap']);

app.controller('Main', function($scope, $http, FileUploader) {

	/*
		Server conf file modelisation
	*/
	var ServerObject = function() {
		this.id = null;
		this.server = {
			port: null,
			host: null,
			queries: []
		};
		this.saved = false;

		this.blankQuery = null;
		this.blankAttr = [];
		this.blankTypeNames = [];

		this.errorMessage = {};

		this.loadJMXTree = function() {
			if (this.server.host && this.server.port) {
				var req = {
					method: 'GET',
					url: 'service/refresh',
					params: {
						host: this.server.host,
						port: this.server.port
					}
				};

				$http(req)
					.error(function() {
						
					});
			}		
		};

		this.save = function() {
			if (!this.saved) {
				if (this.blankQuery && this.blankQuery.obj) {
					this.server.queries.push(this.blankQuery);
					this.blankQuery = null;
				}

				angular.forEach(this.blankAttr, function(attr, i) {
					if (attr && attr.value) {
						if (!this.server.queries[i].attr) {
							this.server.queries[i].attr = [];
						}
						this.queries[i].attr.push(attr.value);
						this.blankAttr[i] = null;
					}
				});

				angular.forEach(this.blankTypeNames, function(typeName, i) {
					if (typeName && typeName.value) {
						if (!this.server.queries[i].typeNames) {
							this.server.queries[i].typeNames = [];
						}
						this.server.queries[i].typeNames.push(typeName.value);
						this.blankTypeNames[i] = null;
					}
				});

				if (this.id) {
					var req = {
						method: 'POST',
						url: 'service/server/_update',
						params: {
							id: this.id
						},
						data: {
							servers: [this.server]
						}
					};

					$http(req)
						.success(function() {
							this.saved = true;
							setTimeout(function() {
								if (this.onSave) {
									this.onSave();
								}
							}, 1000);
						})
						.error(function(response) {
							angular.forEach(response, function(message) {
								this.errorMessage[message.field] = message.message;
							});
						});
				} else {
					var req = {
						method: 'POST',
						url: 'service/server',
						data: { 
							servers: [this.server]
						}
					};

					$http(req)
						.success(function() {
							setTimeout(function() {
								if (this.onSave) {
									this.onSave();
								}
							}, 1000);
						})
						.error(function(response) {
							angular.forEach(response, function(message) {
								this.errorMessage[message.field] = message.message;
							});
						});
				}
			}
		};

		this.removeQuery = function(index) {
			this.server.queries.splice(index, 1);
			this.saved = false;
		};

		this.addBlankQuery = function() {
			if (this.blankQuery && this.blankQuery.obj) {
				this.server.queries.push(this.blankQuery);
			}

			this.blankQuery = {
				obj: null,
				attr: [],
				typeNames: [],
				outputWriters: [
					this.writer
				]
			};
		};

		this.showErrorMessage = function(key) {
			return key in this.errorMessage;
		};
	};
	
	/**
		Servers list 
	**/
	$scope.list = [];

	$scope.updateServersList = function() {
		var req = {
			method: 'GET',
			url: 'service/server/all'
		};

		$http(req)
			.success(function(response) {
				$scope.list = response;

				if ($scope.blankServer) {
					$scope.display($scope.blankServer.server.host, $scope.blankServer.server.port);
				} else if ($scope.server) {
					$scope.display($scope.server.server.host, $scope.server.server.port);
				}
			})
			.error(function(response) {
				$scope.alerts.push({
					type: 'danger',
					message: 'Fail to load severs list.'
				});
			});
	};

	$scope.updateServersList();
	ServerObject.prototype.onSave = $scope.updateServersList();

	$scope.delete = function(host, port) {
		var req = {
			method: 'DELETE',
			url: 'service/server',
			params: {
				host: host,
				port: port
			}
		};

		$http(req)
			.success(function() {
				setTimeout(function() {
					$scope.updateServersList();
				}, 1000);
			})
			.error(function() {
				$scope.alerts.push({
					type: 'danger',
					message: 'Fail to delete the server.'
				});
			});
	};

	$scope.duplicate = function(host, port) {
		var req = {
			method: 'GET',
			url: 'service/server',
			params: {
				host: host,
				port: port
			}
		};

		$http(req)
			.success(function(response) {
				$scope.blankServer = new ServerObject();
				$scope.blankServer.server = response.source.servers[0];
				
				$scope.blankServer.server.host = null;
				$scope.blankServer.server.port = null;
			})
			.error(function() {
				$scope.alerts.push({
					type: 'danger',
					message: 'Fail to load the requested server.'
				});
			});
	};

	$scope.display = function(host, port) {
		var req = {
			method: 'GET',
			url: 'service/server',
			params: {
				host: host,
				port: port
			}
		};

		$http(req)
			.success(function(response) {
				if (response.source) {
					$scope.server = new ServerObject();
					$scope.server.id = response.id;
					$scope.server.server = response.source.servers[0];
					$scope.server.saved = true;

					$scope.server.loadJMXTree();
				} else {
					$scope.server = null;
				}

				$scope.blankServer = null;

			})
			.error(function() {
				$scope.alerts.push({
					type: 'danger',
					message: 'Fail to load the requested server.'
				});
			});
	};


	/**
		Output writer settings
	**/
	$scope.loadSettings = function() {
		$http.get('service/settings')
			.success(function(data) {
				$scope.writer = data;
				if (!$scope.writer.settings) {
					$scope.writer.settings = {};
				}
				ServerObject.prototype.writer = $scope.writer;
			})
			.error(function(response) {
				$scope.alerts.push({
					type: 'danger',
					message: 'Fail to load writer settings.'
				});
			});
	};

	$scope.loadSettings();

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
			case 'com.googlecode.jmxtrans.model.output.RRDToolWriter':
				break;
			case 'com.googlecode.jmxtrans.model.output.StatsDWriter':
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
			.success(function() {
				ServerObject.prototype.writer = $scope.writer;
			})
			.error(function(response) {
				angular.forEach(response, function(message) {
					$scope.errorMessage[message.field] = message.message;
				});
			});
	};


	/** 
		JSON File uploader 
	**/
	$scope.uploader = new FileUploader({ 
		url: 'service/upload' 
	});

	$scope.uploader.onCompleteAll = function() {
		setTimeout(function() {
			$scope.loadSettings();

			var req = {
				method: 'GET',
				url: 'service/server/all'
			};

			$http(req)
				.success(function(response) {
					$scope.list = response;
				})
				.error(function() {
					$scope.alerts.push({
						type: 'danger',
						message: 'Fail to load severs list.'
					});
				});
		}, 1000);
	};



	$scope.alerts = [];

	$scope.errorMessage = {};

	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};


	$scope.addBlankServer = function() {
		$scope.blankServer = new ServerObject();
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
			alerts: '=alerts'
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
			queryIndex: '=index'
		},
		controller: function($scope, $http) {

			$scope.suggestName = function() {
				if (!$scope.nameSuggestions) {
					var req = {
						method: 'GET',
						url: 'service/suggest_name',
						params: {
							host: $scope.server.server.host,
							port: $scope.server.server.port
						}
					};

					$http(req)
						.success(function(response) {
							$scope.nameSuggestions = response;
						});
				}

				$scope.attrSuggestions = null;
			};

			$scope.suggestAttr = function() {
				if (!$scope.attrSuggestions) {
					var req = {
						method: 'GET',
						url: 'service/suggest_attr',
						params: {
							host: $scope.server.server.host,
							port: $scope.server.server.port,
							name: $scope.query.obj
						}
					};

					$http(req)
						.success(function(response) {
							$scope.attrSuggestions = response;
						});
				}
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

				$scope.suggestAttr();
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

			

		},
		templateUrl: 'template/query.html'
	};
});



/** 
	Output writers templates 
**/

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

app.directive('statsDWriterForm', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			writer: '=writer'
		},
		templateUrl: 'template/statsDWriterForm.html'
	};
});

app.directive('rrdToolWriterForm', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			writer: '=writer'
		},
		templateUrl: 'template/rrdToolWriterForm.html'
	};
});
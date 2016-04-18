'use strict';

var app = angular.module('adminApp', ['ui.router']);

app.factory('util', () => {
	var self = {
		obj2arr: (obj) => Object.keys(obj).map((k, i) => ({id: i, key: k, value: obj[k]})),
		filter: (dataContext, query) => {
			if (dataContext.data) {
				dataContext.filtered = self.filterItems(dataContext.data, query, dataContext.page, dataContext.pageSize);
			}
		},
		filterItems: (items, query, page, pageSize) => {
			if (items) {
				if (query)
					items = items.filter(item => item.key.indexOf(query) != -1);
				var maxPage = Math.ceil(items.length / pageSize);
				if (page > maxPage)
					page = maxPage;
				items = items.slice((page - 1) * pageSize, page * pageSize);
			}
			return items;
		}
	};
	return self;
});

app.controller('adminController', ['$scope', ($scope) => {
	
	$scope.routes = [
		{label: 'Configuration', href: '#/config'},
		{label: 'System', href: '#/system'},
		{label: 'About', href: '#/about'}
	];
	
	$scope.getCssClass = (href) => href == window.location.hash ? 'active' : '';
	
}]);

app.controller('systemController', ['$scope', '$filter', '$http', 'util', ($scope, $filter, $http, util) => {
	
	$scope.systemProperties = {page: 1, pageSize: 20, editState: {}};

	$scope.updateSystemProperties = () => {
		$http.get('config/sys-props').success(data => {
			$scope.systemProperties.data = util.obj2arr(data);
			$scope.systemProperties.pageCount = Math.ceil($scope.systemProperties.data.length / $scope.systemProperties.pageSize);
			$scope.systemProperties.pageNums = [];
			for (var i=0; i<$scope.systemProperties.pageCount; i++) {
				$scope.systemProperties.pageNums.push(i+1);
			}
			util.filter($scope.systemProperties, $scope.propKey);
			angular.element('#propKey').focus();
		});
	};
	
	$scope.changePage = (pageNum) => {
		$scope.systemProperties.page = pageNum;
		util.filter($scope.systemProperties, $scope.propKey);
		angular.element('#propKey').focus();
	}
	
	$scope.getCssClass = (href) => href == window.location.hash ? 'active' : '';
	
	$scope.editRecord = (id, $event) => {
		var state = $scope.systemProperties.editState[id] = !$scope.systemProperties.editState[id];
		if (state) {
			// edit mode
			setTimeout(() => angular.element(`[name='text${id}']`).focus(), 200);
		} else {
			// after editing
			var v = $scope.systemProperties.data[id].value = $event.currentTarget.value;
			$http({method: 'POST', url: 'config/sys-props', data: $scope.systemProperties.data[id]})
				.then(resp => {})
				.then(resp => console.log(resp));
		}
	};
	
	$scope.updateSystemProperties();

	$scope.$watch('propKey', (val, o_val) => util.filter($scope.systemProperties, $scope.propKey));
	
}]);

app.controller('aboutController', ['$scope', ($scope) => {
		
		
}]);

app.controller('configController', ['$scope', '$filter', '$http', ($scope, $filter, $http) => {

	$scope.refreshBeanNames = () => {
		$http.get('config/bean-names').success(data => {
			$scope.beanNames = data;
			$scope.filteredBeanNames = $filter('search')($scope.beanNames, $scope.expression);
		});
	};
	
	$scope.fillExpression = (beanName) => {
		$scope.query = '';
		$scope.expression = beanName;
		angular.element('#expression').focus();
	};
	
	$scope.evaluateBeanValue = (expression, httpMethod) => {
		if (expression && !$scope.currentQuery) {
			$scope.currentQuery = {index: $scope.queries.length, expression: expression};
			$scope.queries.push($scope.currentQuery);
			$scope.queryIndex = $scope.currentQuery.index;
			var httpConfig;
			if (httpMethod == 'POST') {
				httpConfig = {method: 'POST', url: 'config/expr', data: {e: expression}};
			} else {
				httpConfig = {method: 'GET', url: 'config/expr/' + encodeURIComponent(expression)};
			}
			$http(httpConfig).then(response => {
				$scope.currentQuery.response = response;
				$scope.currentQuery = undefined;
				
				angular.element('#expression').focus();
			}, response => {
				$scope.currentQuery.response = response;
				$scope.currentQuery = undefined;
				angular.element('#expression').focus();
			});
		}
	};
	
	$scope.refreshBeans = () => {
		$http({method: 'POST', url: 'config/refresh'}).then(response => {
			console.log(response);
			$scope.refreshBeanNames();
		}, response => {
			console.log(response);
		});
	};
	
	$scope.removeQuery = index => $scope.queries[index].removed = true;
	
	$scope.clearQueries = () => {
		$scope.queries = [];
		$scope.expression = '';
		angular.element('#expression').focus();
	};
	
	$scope.isDisplayed = query => !query.removed;
	
	$scope.isObject = angular.isObject;
	
	$scope.changeExpression = e => {
		if ($scope.queryIndex >= 0) {
			if (e.keyCode == 38) { // arrow up to navigate to previous query
				$scope.queryIndex = Math.max(0, Math.min($scope.queryIndex - 1, $scope.queries.length - 1));
				$scope.expression = $scope.queries[$scope.queryIndex].expression;
			} else if (e.keyCode == 40) { // arrow down to navigate to next query
				$scope.queryIndex = Math.max(0, Math.min($scope.queryIndex + 1, $scope.queries.length - 1));
				$scope.expression = $scope.queries[$scope.queryIndex].expression;
			}
		}
	};
	
	$scope.refreshBeanNames();
	$scope.clearQueries();
	
	$scope.$watch('expression', (val, o_val) => {
		$scope.filteredBeanNames = $filter('search')($scope.beanNames, val);
	});
	
	
}]);
	
app.filter('search', () => 
	(items, query) => items && query ? items.filter(item => item.indexOf(query) != -1).sort() : items
);

app.directive('dataView', () => {
	return {
		restrict: 'E',
		templateUrl: 'data-renderer.html'
	};
});

/*
angular.module('configServices', ['ngResource'])
	.factory('Bean', ['$resource', function($resource) {
		return $resource('config/bean/:name/:expression', {}, {
			query: {method: 'GET', params: {name: 'beanName', expression: 'expression'}}
		});

	}]); */

/*
app.config(['$routeProvider', $routeProvider => {
	$routeProvider
		.when('/config', {templateUrl: 'public/views/admin/config.html', controller: 'configController'})
		.when('/system', {templateUrl: 'public/views/admin/system.html', controller: 'systemController'})
		.when('/system/:num', {templateUrl: 'public/views/admin/system.html', controller: 'systemController'})
		.when('/about', {templateUrl: 'public/views/admin/about.html', controller: 'aboutController'});
}]);
*/

app.config(['$stateProvider', '$urlRouterProvider', ($stateProvider, $urlRouterProvider) => {
	// For any unmatched url, redirect to /state1
	//$urlRouterProvider.otherwise("/config");
	// Now set up the states
	  $stateProvider
	    .state('config', {
	      url: '/config',
	      templateUrl: 'public/views/admin/config.html'
	    })
	    .state('system', {
	      url: '/system',
	      templateUrl: 'public/views/admin/system.html'
	    })
	    .state('about', {
	      url: '/about',
	      templateUrl: 'public/views/admin/about.html'
	    });
	
}]);


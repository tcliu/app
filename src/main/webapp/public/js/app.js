'use strict';

var app = angular.module('adminApp', ['ngRoute']);

app.controller('systemController', ['$scope', '$http', ($scope, $http) => {
	
	$scope.updateSystemProperties = () => {
		$http.get('config/sys-props').success(data => {
			$scope.systemProperties = data;
		});
	};
	
	$scope.updateSystemProperties();
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
	(items, query) => items ? items.filter(item => !query || item.indexOf(query) != -1).sort() : null
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


app.config(['$routeProvider', $routeProvider => {
	$routeProvider
		.when('/config', {templateUrl: 'public/views/admin/config.html', controller: 'configController'})
		.when('/system', {templateUrl: 'public/views/admin/system.html', controller: 'systemController'})
		.when('/about', {templateUrl: 'public/views/admin/about.html', controller: 'aboutController'});
}]);


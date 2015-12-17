'use strict';

angular.module('mtpApp')
    .filter('datetime',  ['$filter', function($filter) {
        return function(input) {
            return $filter('date')(input, 'yyyy-MM-dd HH:mm:ss');
        }
    }])
    .filter('time',  ['$filter', function($filter) {
        return function(input) {
            return $filter('date')(input, 'HH:mm:ss');
        }
    }]);
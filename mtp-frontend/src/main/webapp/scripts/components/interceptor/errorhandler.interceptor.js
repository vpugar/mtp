'use strict';

angular.module('mtpApp')
    .factory('errorHandlerInterceptor', function ($q, $rootScope) {
        return {
            'responseError': function (response) {
                if (!(response.status == 401 && response.data.path.indexOf("/api/account") == 0 )){
	                $rootScope.$emit('mtpApp.httpError', response);
	            }
                return $q.reject(response);
            }
        };
    });
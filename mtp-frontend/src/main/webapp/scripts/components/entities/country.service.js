'use strict';

angular.module('mtpApp')
    .factory('Country', function ($resource) {
        return $resource('api/countries/:cca2', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
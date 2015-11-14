'use strict';

angular.module('mtpApp')
    .factory('Transaction', function ($resource) {
        return $resource('api/transactions/:transactionId', {}, {
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

'use strict';

angular.module('mtpApp')
    .factory('Currency', function ($resource) {
        return $resource('api/currencies/:code', {}, {
            'queryWithFilter': { method: 'GET', isArray: true}
        });
    });
'use strict';

angular.module('mtpApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });



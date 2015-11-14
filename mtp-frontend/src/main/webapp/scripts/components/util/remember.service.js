'use strict';

angular.module('mtpApp').factory('RememberService', ['$location', function($location) {

    var stateStore = {};

    return {
        addState: function(state) {
            var current = $location.path();
            stateStore[current] = state;
        },
        getState: function() {
            var current = $location.path();
            return stateStore[current];
        }
    };
}]);
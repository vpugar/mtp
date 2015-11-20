'use strict';

angular.module('mtpApp')
    .factory('Tracker', function ($rootScope, $cookies, $http, $q, Websocket) {
        var subscriber = null;
        var listener = $q.defer();

        function sendActivity() {
            Websocket.send('/topic/activity', {'page': $rootScope.toState.name});
        }

        return {
            subscribe: function () {
                Websocket.subscribe("/topic/tracker", function (data) {
                    listener.notify(JSON.parse(data.body));
                }).then(function (result) {
                    subscriber = result;
                });
            },
            unsubscribe: function () {
                Websocket.unsubscribe(subscriber);
            },
            receive: function () {
                return listener.promise;
            },
            sendActivity: function () {
                sendActivity();
            }
        };
    });

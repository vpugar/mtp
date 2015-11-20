'use strict';

angular.module('mtpApp')
    .factory('Websocket', function ($q, $rootScope) {
        var stompClient = null;
        var connected = $q.defer();
        var alreadyConnectedOnce = false;

        function isConnected() {
            return stompClient != null && stompClient.connected;
        }

        return {
            connect: function (callback) {
                //building absolute path so that websocket doesnt fail when deploying with a context path
                var loc = window.location;
                var url = '//' + loc.host + loc.pathname + 'websocket/mtp';
                var authToken = JSON.parse(localStorage.getItem("ls.token")).access_token;
                url += '?access_token=' + authToken;
                var socket = new SockJS(url);
                stompClient = Stomp.over(socket);
                var headers = {};
                stompClient.connect(headers, function (frame) {
                    connected.resolve("success");
                    if (callback) {
                        callback();
                    }
                    if (!alreadyConnectedOnce) {
                        $rootScope.$on('$stateChangeStart', function (event) {
                            if (callback) {
                                callback();
                            }
                        });
                        alreadyConnectedOnce = true;
                    }
                });
            },
            isConnected: function () {
                return isConnected();
            },
            subscribe: function (topic, callback) {
                return connected.promise.then(function () {
                    return stompClient.subscribe(topic, callback);
                }, null, null);
            },
            unsubscribe: function (subscriber) {
                if (subscriber != null) {
                    subscriber.unsubscribe();
                }
            },
            send: function (topic, obj) {
                if (isConnected()) {
                    stompClient
                        .send(topic,
                            {},
                            JSON.stringify(obj));
                }
            },
            disconnect: function () {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            }
        };
    });

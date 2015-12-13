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
    })
    .factory('TransactionByStatus', function ($q, Websocket) {
        var subscriber = null;
        var listener = $q.defer();

        return {
            subscribe: function () {
                Websocket.subscribe("/topic/rtByStatusActor/all", function (data) {
                    listener.notify(JSON.parse(data.body));
                }).then(function (result) {
                    subscriber = result;
                });
            },
            unsubscribe: function () {
                if (subscriber != null) {
                    Websocket.unsubscribe(subscriber);
                }
            },
            receive: function () {
                return listener.promise;
            }
        };
    })
    .factory('RtTransactionByCountry', function ($q, Websocket) {
        var subscriber = null;
        var listener = $q.defer();

        return {
            subscribe: function () {
                Websocket.subscribe("/topic/rtByOriginatingCountryActor/all", function (data) {
                    listener.notify(JSON.parse(data.body));
                }).then(function (result) {
                    subscriber = result;
                });
            },
            unsubscribe: function () {
                if (subscriber != null) {
                    Websocket.unsubscribe(subscriber);
                }
            },
            receive: function () {
                return listener.promise;
            }
        };
    })
    .factory('RtTransactionByCurrency', function ($q, Websocket) {
        var subscriber = null;
        var listener = $q.defer();

        return {
            subscribe: function () {
                Websocket.subscribe("/topic/rtByCurrencyActor/all", function (data) {
                    listener.notify(JSON.parse(data.body));
                }).then(function (result) {
                    subscriber = result;
                });
            },
            unsubscribe: function () {
                if (subscriber != null) {
                    Websocket.unsubscribe(subscriber);
                }
            },
            receive: function () {
                return listener.promise;
            }
        };
    })
    .factory('PtTransactionByCountry', function ($q, Websocket) {
        var subscriber = null;
        var listener = $q.defer();

        return {
            subscribe: function () {
                Websocket.subscribe("/topic/ptByOriginatingCountryActor/all", function (data) {
                    listener.notify(JSON.parse(data.body));
                }).then(function (result) {
                    subscriber = result;
                });
            },
            unsubscribe: function () {
                if (subscriber != null) {
                    Websocket.unsubscribe(subscriber);
                }
            },
            receive: function () {
                return listener.promise;
            }
        };
    })
    .factory('PtTransactionByCurrency', function ($q, Websocket) {
        var subscriber = null;
        var listener = $q.defer();

        return {
            subscribe: function () {
                Websocket.subscribe("/topic/ptByCurrencyActor/all", function (data) {
                    listener.notify(JSON.parse(data.body));
                }).then(function (result) {
                    subscriber = result;
                });
            },
            unsubscribe: function () {
                if (subscriber != null) {
                    Websocket.unsubscribe(subscriber);
                }
            },
            receive: function () {
                return listener.promise;
            }
        };
    });

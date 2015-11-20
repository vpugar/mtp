'use strict';

angular.module('mtpApp')
    .factory('AggregationSubscription', function ($q, Websocket) {

        var cache = {};

        var AggregationSubscriptionInstance= function(name) {
            this.name = name;
            this.subscriber = null;
            this.listener = $q.defer();
        };

        AggregationSubscriptionInstance.prototype.subscribe = function () {
            var self = this;
            Websocket.subscribe("/topic/" + self.name, function (data) {
                self.listener.notify(JSON.parse(data.body));
            }).then(function (result) {
                self.subscriber = result;
            });
        };

        AggregationSubscriptionInstance.prototype.unsubscribe = function () {
            var self = this;
            Websocket.unsubscribe(self.subscriber);
        };

        AggregationSubscriptionInstance.prototype.receive = function () {
            var self = this;
            return self.listener.promise;
        };

        return {
            getInstance: function(name) {
                if (cache[name]) {
                    return cache[name];
                } else {
                    cache[name] = new AggregationSubscription(name);
                    return cache[name];
                }
            }
        };
    });

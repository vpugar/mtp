'use strict';

angular.module('mtpApp')
    .controller('TransactionsController', function ($scope, $rootScope, $stateParams, ParseLinks, RememberService, Transaction) {

        var now = new Date();
        var tomorrow = new Date();
        tomorrow.setDate(now.getDate() + 1);
        var yesterday = new Date();
        yesterday.setDate(now.getDate() - 1);

        $scope.query = {
            per_page: 20,
            page: 1,
            timeReceivedTo: yesterday,
            timeReceivedFrom: tomorrow
        };
        var query = RememberService.getState();
        if (query) {
            $scope.query = angular.merge(query, $scope.query);
        }

        $scope.maxDate = tomorrow;
        $scope.dateOptions = {
            formatYear: 'yyyy',
            startingDay: 1
        };

        $scope.fromStatus = {
            opened: false
        };
        $scope.toStatus = {
            opened: false
        };

        $scope.loadAll = function () {
            $scope.search($scope.query);
        };
        $scope.search = function (q, page) {
            var query = {};
            angular.copy(q, query);

            // prepare

            if (page) {
                query.page = page;
            }

            // make query
            Transaction.query(query, function (result, headers) {
                $scope.transactions = result;
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.query = query;
                RememberService.addState(query);
            });
        };

        $scope.fromOpen = function($event) {
            $scope.fromStatus.opened = true;
        };

        $scope.toOpen = function($event) {
            $scope.toStatus.opened = true;
        };

        $scope.loadAll();
    });

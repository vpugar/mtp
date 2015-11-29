'use strict';

angular.module('mtpApp')
    .controller('TransactionsController', function ($scope, $rootScope, $stateParams,
                                                    ParseLinks, RememberService, Country, Currency,
                                                    Transaction, defaultQuery) {

        $scope.loading = true;

        var now = new Date();
        var tomorrow = new Date();
        tomorrow.setDate(now.getDate() + 1);

        var query = RememberService.getState();
        if (query) {
            $scope.query = angular.merge(defaultQuery, query);
        } else {
            $scope.query = defaultQuery;
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

            $scope.loading = true;

            var query = {};
            angular.copy(q, query);

            // prepare

            if (page) {
                query.page = page;
            }

            // make query
            if (query.originatingCountryObject && query.originatingCountryObject.length > 0) {
                query.originatingCountry = query.originatingCountryObject[0].cca2;
            } else {
                query.originatingCountry = undefined;
            }
            if (query.currencyFromObject && query.currencyFromObject.length > 0) {
                query.currencyFrom = query.currencyFromObject[0].code;
            } else {
                query.currencyFrom = undefined;
            }
            if (query.currencyToObject && query.currencyToObject.length > 0) {
                query.currencyTo = query.currencyToObject[0].code;
            } else {
                query.currencyTo = undefined;
            }

            Transaction.query(query, function (result, headers) {
                $scope.transactions = result;
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.query = query;
                RememberService.addState(query);
                $scope.loading = false;
            });
        };

        $scope.fromOpen = function($event) {
            $scope.fromStatus.opened = true;
        };

        $scope.toOpen = function($event) {
            $scope.toStatus.opened = true;
        };

        $scope.loadCountries = function($query) {
            return Country.queryWithFilter({action: 'filter', filter: $query}).$promise;
        };

        $scope.loadCurrencies = function($query) {
            return Currency.queryWithFilter({action: 'filter', filter: $query}).$promise;
        };

        $scope.reset = function() {
            $scope.query = defaultQuery;
        };

        $scope.loadAll();
    });

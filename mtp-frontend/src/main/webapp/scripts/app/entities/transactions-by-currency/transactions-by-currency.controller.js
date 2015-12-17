'use strict';

angular.module('mtpApp')
    .controller('TransactionsByCurrencyController', function ($scope, $rootScope, $stateParams, $translate,
                                                             ParseLinks, TransactionByCurrency, timeType) {
        $scope.data = {};
        $scope.timeType = timeType;
        $scope.numberOfUpdates = 0;

        TransactionByCurrency.receive().then(null, null, function (data) {
            data.dateUTC = Date.UTC(data.year, data.month - 1, data.day, data.hour);
            data.date = new Date(data.dateUTC);
            data.currentDateUTC = new Date().getTime();

            addTableData(data);
        });

        function addTableData(data) {

            var key = data.currency;
            var row = $scope.data[key];

            if (row && row.transactionCount != data.transactionCount) {
                $scope.numberOfUpdates++;
                $scope.data[key] = data;
            } else {
                $scope.numberOfUpdates++;
                $scope.data[key] = data;
            }
        }
    });

'use strict';

angular.module('mtpApp')
    .controller('TransactionsByCountryController', function ($scope, $rootScope, $stateParams, $translate,
                                                             ParseLinks, TransactionByCountry, timeType) {
        $scope.data = [];
        $scope.timeType = timeType;

        TransactionByCountry.receive().then(null, null, function (data) {
            data.dateUTC = Date.UTC(data.year, data.month - 1, data.day, data.hour);
            data.date = new Date(data.dateUTC);
            data.currentDateUTC = new Date().getTime();
            addTableData(data);
        });

        function addTableData(data) {
            var existing = false;
            for (var index = 0; index < $scope.data.length; index++) {

                var row = $scope.data[index];

                if (row.year === data.year && row.month === data.month && row.day === data.day && row.hour === data.hour
                    && row.originatingCountry === data.originatingCountry) {
                    existing = true;
                    $scope.data[index] = data;
                }
            }
            if (!existing) {
                $scope.data.push(data);
            }
        }
    });

'use strict';

angular.module('mtpApp')
    .controller('TransactionsByStatusController', function ($scope, $rootScope, $stateParams, $translate,
                                                            ParseLinks, TransactionByStatus, ValidationStatus,
                                                            ChartConsts) {
        $scope.data = [];
        $scope.numberOfUpdates = 0;

        var validationStatusKeys = _.keys(ValidationStatus);
        var series = _.map(_.range(_.size(ValidationStatus)), function (index) {
            return {
                name: $translate.instant(
                    'mtpApp.transactions-by-status.validationStatusMap.' + validationStatusKeys[index]),
                data: []
            };
        });

        $scope.countChartConfig = _.merge({
            title: {
                text: $translate.instant('mtpApp.transactions-by-status.hour-counts')
            },
            yAxis: {
                title: {
                    text: $translate.instant('mtpApp.transactions-by-status.hour-counts')
                }
            },
            series: _.clone(series, true)
        }, ChartConsts.DefaultChartConfig);

        $scope.pointsChartConfig = _.merge({
            title: {
                text: $translate.instant('mtpApp.transactions-by-status.hour-points')
            },
            yAxis: {
                title: {
                    text: $translate.instant('mtpApp.transactions-by-status.hour-points')
                }
            },
            series: _.clone(series, true)
        }, ChartConsts.DefaultChartConfig);

        TransactionByStatus.receive().then(null, null, function (data) {
            data.dateUTC = Date.UTC(data.year, data.month - 1, data.day, data.hour);
            data.date = new Date(data.dateUTC);
            data.currentDateUTC = new Date().getTime();
            addChartData(data);
            addTableData(data);
        });

        function addTableData(data) {
            var existing = false;
            for (var index = 0; index < $scope.data.length; index++) {

                var row = $scope.data[index];

                if (row.year === data.year && row.month === data.month && row.day === data.day && row.hour === data.hour
                    && row.validationStatus === data.validationStatus) {
                    existing = true;
                    $scope.numberOfUpdates++;
                    $scope.data[index] = data;
                }
            }
            if (!existing) {
                $scope.numberOfUpdates++;
                $scope.data.push(data);
            }
        }

        function addChartData(data) {
            var index = ValidationStatus[data.validationStatus].index;
            updateData($scope.countChartConfig.series[index].data, [data.currentDateUTC, data.transactionCount]);
            updateData($scope.pointsChartConfig.series[index].data, [data.currentDateUTC, data.amountPoints]);
        }

        function updateData(seriesData, newValue) {
            seriesData.push(newValue);
            if (seriesData.length > ChartConsts.LineChartEntries) {
                seriesData.shift();
            }
        }
    });

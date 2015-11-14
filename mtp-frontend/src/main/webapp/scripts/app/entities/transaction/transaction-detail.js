'use strict';

angular.module('mtpApp')
    .controller('TransactionDetailController', function ($scope, $rootScope, $stateParams, $state, $translate,
                                                         Transaction, Country) {

        $scope.transactionId = $stateParams.transactionId;

        $scope.load = function () {
            Transaction.get({transactionId: $scope.transactionId}, function (transaction) {

                $scope.transaction = transaction;

                if (transaction.originatingCountry) {
                    Country.get({cca2: transaction.originatingCountry}, function (result) {
                        $scope.country = result;
                    });
                }
            });
        };

        $scope.load();
    });

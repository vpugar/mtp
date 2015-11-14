'use strict';

angular.module('mtpApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('transaction', {
                parent: 'entity',
                url: '/transactions',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'mtp.transaction.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transaction/transactions.html',
                        controller: 'TransactionsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transaction');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('transaction.detail', {
                parent: 'transaction',
                url: '/:transactionId',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'mtp.transaction.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transaction/transaction-detail.html',
                        controller: 'TransactionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transaction');
                        return $translate.refresh();
                    }]
                }
            });
    });

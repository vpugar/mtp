'use strict';

angular.module('mtpApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('rt-transactions-by-currency', {
                parent: 'entity',
                url: '/received-time/transactions-by-currency',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEMO'],
                    pageTitle: 'mtp.transactions-by-currency.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transactions-by-currency/transactions-by-currency.html',
                        controller: 'TransactionsByCurrencyController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transactions-by-currency');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }],
                    TransactionByCurrency: ['RtTransactionByCurrency', function(RtTransactionByCurrency) {
                        return RtTransactionByCurrency;
                    }],
                    timeType: [function() {
                        return 'received-time';
                    }]
                },
                onEnter: function(RtTransactionByCurrency) {
                    RtTransactionByCurrency.subscribe();
                },
                onExit: function(RtTransactionByCurrency) {
                    RtTransactionByCurrency.unsubscribe();
                }
            })
            .state('pt-transactions-by-currency', {
                parent: 'entity',
                url: '/placed-time/transactions-by-currency',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEMO'],
                    pageTitle: 'mtp.transactions-by-currency.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transactions-by-currency/transactions-by-currency.html',
                        controller: 'TransactionsByCurrencyController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transactions-by-currency');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }],
                    TransactionByCurrency: ['PtTransactionByCurrency', function(PtTransactionByCurrency) {
                        return PtTransactionByCurrency;
                    }],
                    timeType: [function() {
                        return 'placed-time';
                    }]
                },
                onEnter: function(PtTransactionByCurrency) {
                    PtTransactionByCurrency.subscribe();
                },
                onExit: function(PtTransactionByCurrency) {
                    PtTransactionByCurrency.unsubscribe();
                }
            });
    });

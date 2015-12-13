'use strict';

angular.module('mtpApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('rt-transactions-by-country', {
                parent: 'entity',
                url: '/received-time/transactions-by-country',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEMO'],
                    pageTitle: 'mtp.transactions-by-country.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transactions-by-country/transactions-by-country.html',
                        controller: 'TransactionsByCountryController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transactions-by-country');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }],
                    TransactionByCountry: ['RtTransactionByCountry', function(RtTransactionByCountry) {
                        return RtTransactionByCountry;
                    }],
                    timeType: [function() {
                        return 'received-time';
                    }]
                },
                onEnter: function(RtTransactionByCountry) {
                    RtTransactionByCountry.subscribe();
                },
                onExit: function(RtTransactionByCountry) {
                    RtTransactionByCountry.unsubscribe();
                }
            })
            .state('pt-transactions-by-country', {
                parent: 'entity',
                url: '/placed-time/transactions-by-country',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEMO'],
                    pageTitle: 'mtp.transactions-by-country.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transactions-by-country/transactions-by-country.html',
                        controller: 'TransactionsByCountryController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transactions-by-country');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }],
                    TransactionByCountry: ['PtTransactionByCountry', function(PtTransactionByCountry) {
                        return PtTransactionByCountry;
                    }],
                    timeType: [function() {
                        return 'placed-time';
                    }]
                },
                onEnter: function(PtTransactionByCountry) {
                    PtTransactionByCountry.subscribe();
                },
                onExit: function(PtTransactionByCountry) {
                    PtTransactionByCountry.unsubscribe();
                }
            });
    });

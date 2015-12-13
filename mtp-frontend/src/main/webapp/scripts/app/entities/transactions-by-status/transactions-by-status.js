'use strict';

angular.module('mtpApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('rt-transactions-by-status', {
                parent: 'entity',
                url: '/received-time/transactions-by-status',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEMO'],
                    pageTitle: 'mtp.transactions-by-status.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/transactions-by-status/transactions-by-status.html',
                        controller: 'TransactionsByStatusController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transactions-by-status');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                },
                onEnter: function(TransactionByStatus) {
                    TransactionByStatus.subscribe();
                },
                onExit: function(TransactionByStatus) {
                    TransactionByStatus.unsubscribe();
                }
            });
    });

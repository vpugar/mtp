angular.module('mtpApp')
    .constant('ValidationStatus', {
        OK: {
            description: "OK",
            index: 0
        },
        InvalidAmount: {
            description: "Invalid amount",
            index: 1
        },
        InvalidRate: {
            description: "Invalid rate",
            index: 2
        },
        InvalidCountry: {
            description: "Invalid country",
            index: 3
        },
        InvalidFromCurrency: {
            description: "Invalid 'from' currency",
            index: 4
        },
        InvalidToCurrency: {
            description: "Invalid 'to' currency",
            index: 5
        }
    });
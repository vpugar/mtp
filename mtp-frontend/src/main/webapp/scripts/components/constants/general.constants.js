angular.module('mtpApp')
    .constant('ChartConsts', {
        LineChartEntries: 120,
        DefaultChartConfig: {
            options: {
                chart: {
                    type: 'line'
                },
                tooltip: {
                    style: {
                        padding: 10,
                        fontWeight: 'bold'
                    }
                }
            },
            loading: false,
            xAxis: {
                type: 'datetime',
                minRange: 30 * 1000
            },
            useHighStocks: false
        }
    })
    .constant('TableConsts', {
        Page: 1,
        PerPage: 20
    });
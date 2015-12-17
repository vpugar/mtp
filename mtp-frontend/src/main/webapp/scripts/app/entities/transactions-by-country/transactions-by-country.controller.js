'use strict';

angular.module('mtpApp')
    .controller('TransactionsByCountryController', function ($scope, $rootScope, $stateParams, $translate,
                                                             $http, olData, olHelpers,
                                                             ParseLinks, TransactionByCountry, timeType) {
        $scope.data = {};
        $scope.timeType = timeType;
        $scope.numberOfUpdates = 0;
        var features = {};

        var formatText = function(name, data) {
            return data ? name + ' (' + data.transactionCount + ')' : name;
        };

        TransactionByCountry.receive().then(null, null, function (data) {
            data.dateUTC = Date.UTC(data.year, data.month - 1, data.day, data.hour);
            data.date = new Date(data.dateUTC);
            data.currentDateUTC = new Date().getTime();
            addTableData(data);
        });

        function addTableData(data) {

            var key = data.originatingCountry;
            var row = $scope.data[key];
            var feature = features[key];

            if (row && row.transactionCount != data.transactionCount) {
                $scope.numberOfUpdates++;
                $scope.data[key] = data;

                if ($scope.selectedData && $scope.selectedData.originatingCountry === key) {
                    $scope.selectedData = data;
                }

                if (feature) {
                    //feature.set('text', formatText(key, data));
                    feature.getGeometry().changed();
                }
            } else {
                $scope.numberOfUpdates++;
                $scope.data[key] = data;
            }
        }

        // Map part

        var continentProperties= {
            "009": {
                name: 'Oceania',
                colors: [ '#CC0066', '#993366', '#990066', '#CC3399', '#CC6699' ]
            },
            "019": {
                name: 'America',
                colors: [ '#006699', '#336666', '#003366', '#3399CC', '#6699CC' ]
            },
            "150": {
                name: 'Europe',
                colors: [ '#FF0000', '#CC3333', '#990000', '#FF3333', '#FF6666' ]
            },
            "002": {
                name: 'Africa',
                colors: [ '#00CC00', '#339933', '#009900', '#33FF33', '#66FF66' ]
            },
            "142": {
                name: 'Asia',
                colors: [ '#FFCC00', '#CC9933', '#999900', '#FFCC33', '#FFCC66' ]
            }
        };
        // Get a country paint color from the continents array of colors
        var getColor = function(country) {
            if (!country || !country["region-code"]) {
                return "#FFF";
            }
            var colors = continentProperties[country["region-code"]].colors;
            var index = country["alpha-3"].charCodeAt(0) % colors.length ;
            return colors[index];
        };

        var count= 0;
        var getStyle = function(feature) {
            var key3 = feature.getId();
            var country = $scope.countries[key3];
            var text = key3;
            if (country) {
                var key2 = country['alpha-2'];
                if (features[key2] === undefined) {
                    features[key2] = feature;
                }
                var data = $scope.data[key2];
                text = formatText(key2, data) + (count++);
            }
            var style = olHelpers.createStyle({
                fill: {
                    color: getColor($scope.countries[key3]),
                    opacity: 0.4
                },
                stroke: {
                    color: 'white',
                    width: 3
                },
                text: new ol.style.Text({
                    scale: 0.8,
                    text: text,
                    fill: new ol.style.Fill({
                        color: '#000'
                    })
                })
            });
            return [ style ];
        };

        angular.extend($scope, {
            center: {
                lat: 30,
                lon: 0,
                zoom: 2
            },
            geojson: {
                name: 'geojson',
                source: {
                    type: 'GeoJSON',
                    url: '/maps/data/countries.geo.json'
                },
                style: getStyle
            },
            defaults: {
                events: {
                    layers: [ 'mousemove', 'click' ]
                }
            }
        });
        // Get the countries data from a JSON
        $http.get("/maps/data/countries.json").success(function(data, status) {
            // Put the countries on an associative array
            $scope.countries = {};
            for (var i=0; i< data.length; i++) {
                var country = data[i];
                $scope.countries[country['alpha-3']] = country;
            }
        });
        olData.getMap().then(function(map) {
            var previousFeature;
            var overlay = new ol.Overlay({
                element: document.getElementById('countrybox'),
                positioning: 'center-center',
                offset: [0, 80],
                position: [0, 0]
            });
            var overlayHidden = true;
            // Mouse over function, called from the Leaflet Map Events
            $scope.$on('openlayers.layers.geojson.mousemove', function(event, feature, olEvent) {
                $scope.$apply(function(scope) {
                    scope.selectedCountry = feature ? $scope.countries[feature.getId()] : '';
                    if (scope.selectedCountry) {
                        scope.selectedData = $scope.data[scope.selectedCountry['alpha-2']];
                    }
                });
                if (!feature) {
                    map.removeOverlay(overlay);
                    overlayHidden = true;
                    return;
                } else if (overlayHidden) {
                    map.addOverlay(overlay);
                    overlayHidden = false;
                }
                overlay.setPosition(map.getEventCoordinate(olEvent));
                if (feature) {
                    feature.setStyle(olHelpers.createStyle({
                        fill: {
                            color: '#FFF'
                        }
                    }));
                    if (previousFeature && feature !== previousFeature) {
                        previousFeature.setStyle(getStyle(previousFeature));
                    }
                    previousFeature = feature;
                }
            });
        });

    });

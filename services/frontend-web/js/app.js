'use strict';


// Declare app level module which depends on filters, and services
var acuitra = angular.module('acuitra', [
  'ngRoute',
  'ui.bootstrap',
  'acuitra.controllers',
  'acuitra.services',
  'acuitra.filters',
  'acuitra.directives'      
]).
config(['$routeProvider', function($routeProvider) {
}]);
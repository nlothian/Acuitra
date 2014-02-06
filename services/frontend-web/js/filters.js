'use strict';

/* Filters */

angular.module('acuitra.filters', []).
  filter('interpolate', [function() {
    return function(text) {
      //return String(text).replace(/\%VERSION\%/mg, version);
      return text;
    }
  }]);

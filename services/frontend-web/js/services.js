'use strict';

/* Services */
angular.module('acuitra.services', []).
  service('configService', ['$http', function($http) {
        var config = {};
        
        $http.get('/config.js').success(function(data) {
          console.log(data);
          config = data;
        });          
  
        
        this.get = function() {
          return config;
        };
        
    }]);
  

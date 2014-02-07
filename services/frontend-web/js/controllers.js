                                                                                                                                             'use strict';

/* Controllers */

angular.module('acuitra.controllers', []).
  controller('MyCtrl1', [function() {

  }])
  .controller('MyCtrl2', [function() {

  }])
  .controller('QuestionCtrl', ['$scope', '$http', 'configService', function($scope, $http, configService) {

    $scope.gotPosition = function(position) {
      $scope.position = position;
      console.log(position);
      $scope.getNearbyResources(position);
    }
    
    $scope.geoLocationError = function(error) {
      console.log(error);
    } 
    
    $scope.getLocation = function() {
      if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition($scope.gotPosition, $scope.geoLocationError);
      }        
    }
    
    $scope.getLocation();      
  
    $scope.model = {};
    $scope.keyPressed = function(ev) {
      
      if (ev.which==13) {        
        // When enter is pressed  
        $scope.hideSamples = true;

        $scope.model.inprogress = true;

        // Ask the question
        $http({method: 'GET', url: configService.get().questionServiceUrl + $scope.questionbox.question}).
          success(function(data, status, headers, config) {
            // get the answer        
            $scope.model.inprogress = false;
            $scope.model.response = data;
            if (data.answers && data.answers.length == 0) {
              $scope.model.noAnswerFound = true;
            }
          }).
          error(function(data, status, headers, config) {
            $scope.model.inprogress = false;
          });
      } else {
          $scope.model = {};
      }        
    }
    
    $scope.getNearbyResources = function(position) {      
      $http({method: 'GET', url: configService.get().nearbyServiceUrl + "?longitude=" + position.coords.longitude + "&latitude=" + position.coords.latitude + "&maxResults=6"}).
        success(function(data, status, headers, config) {          
          $scope.nearby = data;       
          
          $scope.nearbyResourceAry = [];
          
          angular.forEach(data.results.bindings, function(value, key) {
            //console.log(value);
            if ("uri" == value.resource.type) { 
              var resource = encodeURIComponent("<" + value.resource.value + ">")
              $http.get(configService.get().resourceDetailsServiceUrl + resource + "/basic").success(function(data) {
                //console.log(data);
                $scope.nearbyResourceAry.push(data); 
                $scope.showNearby = true;              
              });
            }
          });              
          
           
        }).
        error(function(data, status, headers, config) {
          
        });    
    }
        
  }]);  
  
  
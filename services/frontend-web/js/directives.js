'use strict';

/* Directives */


angular.module('acuitra.directives', []).
  directive('nearbythumbnail', function() {
    return {
      link: function(scope, element, attrs) {
        element.bind("load" , function(event){
          // we do this to stop showing broken image links 

          scope.show = true;
        });    
      }    
    }
  });    
angular.module('xp.filters', [])




.filter('fromNow', function() {
  return function(date) {
    return moment(date).fromNow();
  }
});
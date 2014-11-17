app




.directive('experience', ["MoodGrid", "Restangular", function (MoodGrid, Restangular) {

	return {
		restrict: 'E',
		scope: {
			experience: '='
		},
		templateUrl:'partials/directives/experience.html',
		link: function (scope, element, attrs) {

			scope.getMoodStyle = function(mood) {

				var m ;
				if (mood)
					m = mood ;
				else
					m = {valence:0, arousal:0} ;

				return {
					color: MoodGrid.getColor(m.valence, m.arousal) 
				}
			}


			scope.addTag = function() {

				console.log("adding " + scope.newTag) ;

				if (!scope.newTag)
					return ;

				var exp = _.clone(scope.experience) ;

				if (!exp.tags)
					exp.tags = [] ;

				exp.tags.push(scope.newTag) ;
				exp.tags = _.uniq(exp.tags) ;
				
				Restangular.all('experiences').post(exp)
				.then(
					function (newExp) {
						console.log(newExp) ;
						scope.experience = newExp ;
						scope.newTag = undefined ;
					}, 
					function (error) {
						console.log(error) ;
					}
				) ;

				
			}

			scope.removeTag = function(tag) {

				if (!tag)
					return ;

				var exp = _.clone(scope.experience) ;

				exp.tags = _.without(exp.tags, tag) ;

				Restangular.all('experiences').post(exp)
				.then(
					function (newExp) {
						console.log(newExp) ;
						scope.experience = newExp ;
					}, 
					function (error) {
						console.log(error) ;
					}
				) ;

				
			}

			scope.getBackgroundStyle = function() {

				var mb,ma ;

				if (!scope.experience || !scope.experience.moodBefore)
					mb = {valence:0, arousal:0} ;
				else
					mb = scope.experience.moodBefore ;

				if (!scope.experience || !scope.experience.moodAfter)
					ma = {valence:0, arousal:0} ;
				else
					ma = scope.experience.moodAfter ;

				return {
					backgroundColor: "black",
					backgroundImage: "linear-gradient(to right, " + MoodGrid.getColor(mb.valence, mb.arousal) + "," + MoodGrid.getColor(ma.valence, ma.arousal) + ")"
				}
			}

			scope.setFilterMoodBefore = function() {

				scope.$parent.setFilterMoodBefore(scope.experience.moodBefore) ;
			}

			scope.setFilterMoodAfter = function() {

				scope.$parent.setFilterMoodAfter(scope.experience.moodAfter) ;
			}

			scope.addTagFilter = function(tag) {

				scope.$parent.addTagFilter(tag) ;
			}





		}
	}



}])




.directive('onEnter', function() {
        return function(scope, element, attrs) {
            element.bind("keydown keypress", function(event) {
                if(event.which === 13) {
                    scope.$apply(function(){
                        scope.$eval(attrs.onEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    })












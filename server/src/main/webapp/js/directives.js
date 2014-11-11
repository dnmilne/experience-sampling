app





.directive('moodCanvas', function () {

	

	return {
		restrict: 'E',
		scope: {
			valence:'=',
			arousal:'='
		},
		link: function (scope, element, attrs) {

			var width = 300 ;
			var height = 300 ;
			var margin = {top: -5, right: -5, bottom: -5, left: -5} ;

			var xScale = d3.scale.linear().domain([-1,1]).range([0,width]) ;
			var yScale = d3.scale.linear().domain([-1,1]).range([height,0]) ;

			var drag = d3.behavior.drag()
			    .on("dragstart", dragstarted)
			    .on("drag", dragged)
			    .on("dragend", dragended) ;

			var svg = d3.select(element[0]).append("svg")
			    .attr("width", width + margin.left + margin.right)
			    .attr("height", height + margin.top + margin.bottom)
			    .attr("cursor", "default")
			  .append("g")
			    .attr("transform", "translate(" + margin.left + "," + margin.right + ")") ;

			var rect = svg.append("rect")
			    .attr("width", width)
			    .attr("height", height)
			    .style("fill", "none")
			    .style("pointer-events", "all");

			var container = svg.append("g");


			var handle = container.append("g")
				  .append("circle")
			      .attr("class", "dot")
			      .attr("r", 15)
			      .attr("cx", xScale(scope.valence))
			      .attr("cy", yScale(scope.arousal))
			      .attr("cursor", "pointer")
			      .call(drag);

			function dragstarted() {
				console.log("drag started ") ;


			  d3.event.sourceEvent.stopPropagation();
			  handle.classed("dragging", true);
			}

			function dragged() {

				console.log("dragged") ;
			  	handle.attr("cx", d3.event.x).attr("cy", d3.event.y);
			}

			function dragended() {
			  	handle.classed("dragging", false);

			  	scope.$apply(function () {
		            scope.valence = xScale.invert(handle.attr("cx")) ;
					scope.arousal = yScale.invert(handle.attr("cy")) ;
		        });
			}


		}

	}
})







.directive('apiParameter', function ($sanitize, $modal) {

	return {
		restrict: 'E',
		scope: {
			param: '=',
			paramIndex: '=',
			models: '='
		},
		templateUrl:'apiParameter.html',
		link: function (scope, element, attrs) {

			scope.getEnumDescription = function(param) {

				return getEnumDescription(param.enum) ;
			}
		}
	} 
}) 





.directive('apiResponse', function ($sanitize) {

	return {
		restrict: 'E',
		scope: {
			response: '=',
			responseIndex: '=',
			models: '='
		},
		templateUrl:'apiResponse.html',
		link: function (scope, element, attrs) {

			scope.getResponseCodeClass = function(code) {

				if (code >= 200 && code < 300)
					return "label-success" ;
				else
					return "label-danger" ;
			}

		}
	} 
}) 


.directive('apiObjectBadge', function ($sanitize, $modal) {

	return {
		restrict: 'E',
		scope: {
			responseModel: '=',
			propertyType: '=',
			propertyRef: '=',
			models: '='
		},
		templateUrl:'apiObjectBadge.html',
		link: function (scope, element, attrs) {

			scope.$watch("responseModel", handleChange(), true) ;
			scope.$watch("propertyType", handleChange(), true) ;
			scope.$watch("propertyRef", handleChange(), true) ;

			function handleChange() {

				if (!scope.responseModel && !scope.propertyType && !scope.propertyRef)
					return ;

				if (scope.responseModel) {

					var genericsRegex = /(.+)[«](.+)[»]/g;

					var match = genericsRegex.exec(scope.responseModel) ;

					if(match) {
						scope.objectName = match[2] ;
						scope.fullObjectName = match[1] + "<" + match[2] + ">"
					} else {
						scope.objectName = scope.responseModel ;
						scope.fullObjectName = scope.responseModel ;
					}



				} else if (scope.propertyType) {

					scope.objectName = scope.propertyType ;
					scope.fullObjectName = scope.propertyType ;

				} else {
					scope.objectName = scope.propertyRef ;
					scope.fullObjectName = scope.propertyRef ;
				}

				if (scope.models)
					scope.object = scope.models[scope.objectName] ;
			}

			scope.showModal = function() {

				var modalInstance = $modal.open({
					templateUrl: 'modalApiObject.html',
					controller: ModalApiObjectCtrl,
					size: 'large',
					resolve: {
						object: function () {
							return scope.object;
						},
						models: function () {
							return scope.models;
						}
					}
				});
			}
		}
	} 
}) ;



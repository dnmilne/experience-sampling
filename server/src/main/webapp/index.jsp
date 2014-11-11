<!DOCTYPE html>
<meta charset="utf-8">

<html lang="en" ng-app="xp">

  <title>Experience Sampling App</title>

  <!--jquery & lodash -->
  <script type="text/javascript"  src="bower_components/jquery/dist/jquery.min.js"></script>
  <script type="text/javascript"  src="bower_components/lodash/dist/lodash.min.js"></script>

  <!--angularjs -->
  <script type="text/javascript"  src="bower_components/angular/angular.js"></script>
  <script type="text/javascript"  src="bower_components/angular-route/angular-route.min.js"></script>
  <script type="text/javascript"  src="bower_components/angular-sanitize/angular-sanitize.min.js"></script>
  <script type="text/javascript"  src="bower_components/angular-cookies/angular-cookies.min.js"></script>
  <script type="text/javascript"  src="bower_components/angular-base64/angular-base64.min.js"></script>
  <script type="text/javascript"  src="bower_components/restangular/dist/restangular.min.js"></script>

  <!--d3, for sexy visualizations -->
  <script type="text/javascript"  src="bower_components/d3/d3.min.js"></script>


  <!--bootstrap and ui-bootstrap, used for styling and common widgets like modals-->
  <script type="text/javascript"  src="bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
  <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.min.css">

  <!--font awesome, used for ui icons -->
  <link rel="stylesheet" href="bower_components/fontawesome/css/font-awesome.min.css">

  <!--showdown, used to convert markdown to html-->
  <script type="text/javascript"  src="bower_components/showdown/compressed/showdown.js"></script>

   <!--my app -->
  <script type="text/javascript" src="js/app.js"></script>
  <link rel="stylesheet" href="css/style.css">
  <script type="text/javascript" src="js/controllers.js"></script>
  <script type="text/javascript" src="js/directives.js"></script>
  <script type="text/javascript" src="js/services.js"></script>
  <script type="text/javascript" src="js/filters.js"></script>
  <script type="text/javascript" src="js/util.js"></script>

</head>

<body>

  <div ng-view></div>



</body>
</html>

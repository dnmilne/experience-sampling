function getEnumDescription(enumArray) {

	if (!enumArray)
		return "" ;

	var str = "" ;

	_.each(enumArray, function(e, index) {

		if (index == 0) {
			str = "" ;
		} else if (index < enumArray.length-1) {
			str = str + ", " ;
		} else {
			str = str + " or " ;
		}

		str = str + "<em>" + e + "</em>" ;
	}) ;

	return "(" + str + ")" ;
}
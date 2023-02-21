//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Plugin JS - Location.js
// Plugin Use - Displaying custom google map with customized information (tooltip) on hover.
// Author - Nagesh Modi
// Organization - SoftServ Solutions Pvt. Ltd., (India)
// Email - info@softserv.in
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Server variables.
var HostName = 'localhost' 	//Enter the network name of the machine where MySQL database is running.
//If its running on the same machine where AwareIM is installed, leave it as localhost 
var DBName = 'staydry';   	//If your bsv is in Testing mode, DBName will be basdbtest. If your bsv is in publish mode, DBName will be basdb.
//If its a separate database, enter your database name 
var BSVName = 'STAYDRY';   		//Please replace BSV name Map with your BSV Name in Upparcase
var googleApiKey = 'AIzaSyDuRGpeiv6-7fPJ6fZQVDJGcE5j7Pd9kgs';


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


document.write('<scr' + 'ipt type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=' + googleApiKey + '&v=3.31" ></scr' + 'ipt>');

// Create Base64 Object for encoding data
var Base64 = {
    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
    encode: function (e) {
        var t = "";
        var n, r, i, s, o, u, a;
        var f = 0;
        e = Base64._utf8_encode(e);
        while (f < e.length) {
            n = e.charCodeAt(f++);
            r = e.charCodeAt(f++);
            i = e.charCodeAt(f++);
            s = n >> 2;
            o = (n & 3) << 4 | r >> 4;
            u = (r & 15) << 2 | i >> 6;
            a = i & 63;
            if (isNaN(r)) {
                u = a = 64
            } else if (isNaN(i)) {
                a = 64
            }
            t = t + this._keyStr.charAt(s) + this._keyStr.charAt(o) + this._keyStr.charAt(u) + this._keyStr.charAt(a)
        }
        return t
    },
    decode: function (e) {
        var t = "";
        var n, r, i;
        var s, o, u, a;
        var f = 0;
        e = e.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        while (f < e.length) {
            s = this._keyStr.indexOf(e.charAt(f++));
            o = this._keyStr.indexOf(e.charAt(f++));
            u = this._keyStr.indexOf(e.charAt(f++));
            a = this._keyStr.indexOf(e.charAt(f++));
            n = s << 2 | o >> 4;
            r = (o & 15) << 4 | u >> 2;
            i = (u & 3) << 6 | a;
            t = t + String.fromCharCode(n);
            if (u != 64) {
                t = t + String.fromCharCode(r)
            }
            if (a != 64) {
                t = t + String.fromCharCode(i)
            }
        }
        t = Base64._utf8_decode(t);
        return t
    },
    _utf8_encode: function (e) {
        e = e.replace(/\r\n/g, "\n");
        var t = "";
        for (var n = 0; n < e.length; n++) {
            var r = e.charCodeAt(n);
            if (r < 128) {
                t += String.fromCharCode(r)
            } else if (r > 127 && r < 2048) {
                t += String.fromCharCode(r >> 6 | 192);
                t += String.fromCharCode(r & 63 | 128)
            } else {
                t += String.fromCharCode(r >> 12 | 224);
                t += String.fromCharCode(r >> 6 & 63 | 128);
                t += String.fromCharCode(r & 63 | 128)
            }
        }
        return t
    },
    _utf8_decode: function (e) {
        var t = "";
        var n = 0;
        var r = c1 = c2 = 0;
        while (n < e.length) {
            r = e.charCodeAt(n);
            if (r < 128) {
                t += String.fromCharCode(r);
                n++
            } else if (r > 191 && r < 224) {
                c2 = e.charCodeAt(n + 1);
                t += String.fromCharCode((r & 31) << 6 | c2 & 63);
                n += 2
            } else {
                c2 = e.charCodeAt(n + 1);
                c3 = e.charCodeAt(n + 2);
                t += String.fromCharCode((r & 15) << 12 | (c2 & 63) << 6 | c3 & 63);
                n += 3
            }
        }
        return t
    }
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;
    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};
 function urlencode(text) {
        return encodeURIComponent(text).replace(/!/g,  '%21')
                                       .replace(/'/g,  '%27')
                                       .replace(/\(/g, '%28')
                                       .replace(/\)/g, '%29')
                                       .replace(/\*/g, '%2A')
                                       .replace(/%20/g, '+');
    }
var map;
//get value of RegularUser ID from URL
var CID = getUrlParameter('CustomerID');
//encoding RegularUser ID
var options;
var WholeContent = "";

var geocoder, controlUI;
var myLatlng = { lat: 41.257160, lng: -95.995102 };
var CAction = getUrlParameter('action');
// check DOM Ready
$(document).ready(function () {
    // execute
    // map options
    //For map settings and default center 
    geocoder = new google.maps.Geocoder();
    options = {
        zoom: 4,
        center: myLatlng, // centered USA
        mapTypeControl: true,
        fullscreenControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
            mapTypeIds: ['roadmap', 'satellite']
        }
    };
    // init map
    map = new google.maps.Map(document.getElementById('map_canvas'), options);
    var element = $('#map_canvas');
    // Create the DIV to hold the control and call the CustomControl() constructor passing in this DIV.
    var customControlDiv = document.createElement('div');
    var customControl = new CustomControl(customControlDiv, map);
    customControlDiv.index = 1;
    map.controls[google.maps.ControlPosition.RIGHT_TOP].push(customControlDiv);

}
    );

function CustomControl(controlDiv, map) {
    // Set CSS for the control border
    controlUI = document.createElement('div');
    controlUI.style.background = '#00bfff';
    controlUI.style.borderStyle = 'solid';
    controlUI.style.borderWidth = '1px';
    controlUI.style.borderColor = '#ccc';
    controlUI.style.height = '23px';
    controlUI.style.marginTop = '5px';
    controlUI.style.marginLeft = '-6px';
    controlUI.style.paddingTop = '1px';
    controlUI.style.cursor = 'pointer';
    controlUI.style.textAlign = 'center';
    controlUI.title = 'Click to open Map in PDF';
    controlDiv.appendChild(controlUI);
    // Set CSS for the control interior
    var controlText = document.createElement('div');
    controlText.style.fontFamily = 'Arial,sans-serif';
    controlText.style.fontSize = '15px';
    controlText.style.paddingLeft = '4px';
    controlText.style.paddingRight = '4px';
    controlText.style.marginTop = '0px';
    controlText.innerHTML = 'Open PDF';
    controlUI.appendChild(controlText);

    // Setup the click event listeners
    google.maps.event.addDomListener(controlUI, 'click', function () {
        //URL of Google Static Maps.
        //  window.open("/Location/index.jsp?CustomerID=" + CID + "&action=print", "_blank");
        var staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap";

        //Set the Google Map Size.
        staticMapUrl += "?size=400x400";

        //Set the Google Map Type.
        staticMapUrl += "&maptype=roadmap";

        //Loop and add Markers.
        for (var i = 0; i < markers.length; i++) {
            staticMapUrl += "&markers=color:green%7Clabel:" + (i + 1) + "%7C" + urlencode(fullAddress[i]);
        }
        console.log(staticMapUrl);
        var date = new Date();
        var hour = date.getHours();
        var min = date.getMinutes();
        var month=date.getMonth();
        var FileName = "PermaSeal_" +date.getFullYear()+ "-" + (month+1) + "-" + date.getDate() + "_" + hour  + "-"  + min + "-" + date.getSeconds();
        $.ajax({
            type: "POST",
            url: "ParseHtml?",
            data: {
                "MapURL": staticMapUrl,
                "ContentValues": WholeContent,
                "FileName": FileName               
            },            
            dataType: "json",
            success: function (data) {
              
                window.open(data.Success + FileName + ".pdf","_blank")
            },
            error: function (xhr, status) {
                alert('fail');
            }
        });
       
    });
    // Define the string
    var custID = Base64.encode(CID);
    var secure_DBName = Base64.encode(DBName);
    var secure_BSVName = Base64.encode(BSVName);
    // created by Nagesh date:20/07/2017 SoftServ
    //ajax to fetch data from AwareIM BO System Settings and pass those values to java class
    $.ajax({
        type: "POST",
        url: "GetLocationDetails?",
        data: {
            "DBName": secure_DBName,
            "BSVName": secure_BSVName,
            "CustomerID": custID
        },
        dataType: "json",
        success: function (data) {        
            // get processed data from database.
            //display(data);
			repsList(data);
        },
        error: function (xhr, status) {
            alert("An error has occured. Error message: " + status);
        }
    });
}

function repsList(data) {
	console.log();
	//display(data);
	//console.log(Object.values(data));
	var color = [
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	"red", "blue", "green", "grey", "orange", "purple", "yellow", "black", "white",
	];
	var i = 0;
	for(var key in data){
	    if(data.hasOwnProperty(key)) {
			display(data[key], color[i]);
			i++;
	    } 
	}
}

function display(data, color) {
	var fullAddress = new Array();
	var firstName = new Array();
	var lastName = new Array();
	var startTime = new Array();
	var markers = new Array();
	var distances = new Array();
	var durations = new Array();
	var lat_lng = new Array();
	console.log("color: "+color);
    var iglobal;
	var pin=1;
    var newI, marker;
    var path = new google.maps.MVCArray();
    var directionsDisplay;
    var service = new google.maps.DirectionsService();
    var poly = new google.maps.Polyline({ map: map, strokeColor: color});
	console.log(data);
    for (i = 0; i < data.fullAddress.length; i++) { 
		iglobal=i;
		if(data.Lat[i]=='')
		{
			alert('"'+data.fullAddress[i]+'" – address is incorrect.');
			continue;
		}
    	    
		//var iconImage = "http://mt.googleapis.com/maps/vt/icon/name=icons/spotlight/spotlight-waypoint-a.png&text=" + (pin) + "&psize=16&font=fonts/Roboto-Regular.ttf&color=ff333333&ax=44&ay=48&scale=1"; 
		var iconImage = "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_"+color+pin+".png";
		pin++;
		var latlng = new google.maps.LatLng(data.Lat[iglobal],data.Long[iglobal]);		
		lat_lng.push(latlng);
		fullAddress.push(data.fullAddress[iglobal]);
		firstName.push(data.firstName[iglobal]);
		lastName.push(data.lastName[iglobal]);
		startTime.push(data.startTime[iglobal]);
		markers.push({ position: latlng, map: map, icon: iconImage });
    }
	
	if(lat_lng.length > 1)
	{
	    for (var i = 0; i < lat_lng.length; i++) {
			/*
			var Colors = [
			    "#FF0000", 
			    "#00FF00", 
			    "#0000FF", 
			    "#FFFFFF", 
			    "#000000", 
			    "#FFFF00", 
			    "#00FFFF", 
			    "#FF00FF"
			];
	
			var PathStyle = new google.maps.Polyline({
			    path: [lat_lng[i], lat_lng[i+1]],
			    strokeColor: Colors[i],
			    strokeOpacity: 1.0,
			    strokeWeight: 2,
			    map: map
			});
			*/
		
			//var iconImage, distance, duration, contentString;
	        var iconImage, distance, duration;
	        var src = lat_lng[i];
	        var des = lat_lng[i + 1];
	        path.push(src);
	        poly.setPath(path);
	        service.route({
	            origin: src,
	            destination: des,
	            travelMode: google.maps.DirectionsTravelMode.DRIVING,
	            avoidHighways: false,
	            avoidTolls: false
	        }, function (result, status) {
	            if (status == google.maps.DirectionsStatus.OK) {
	                distance = result.routes[0].legs[0].distance.text;
	                duration = result.routes[0].legs[0].duration.text;
	                //iconImage = "http://mt.googleapis.com/maps/vt/icon/name=icons/spotlight/spotlight-waypoint-a.png&text=" + (i + 1) + "&psize=16&font=fonts/Roboto-Regular.ttf&color=ff333333&ax=44&ay=48&scale=1";
	                iconImage = "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_"+color+pin+".png";
					distances.push(distance);
	                durations.push(duration);
	
	                //Call it when we are all fone.
	                SetupMarkers(distances, markers, durations, firstName, lastName, fullAddress, startTime, durations);
	            }
	        });
	    }
	//PathStyle.setMap(map);
	}
	else {
		SetupMarkers(distances, markers, durations, firstName, lastName, fullAddress, startTime, durations);
	}
}

function SetupMarkers(distances, markers, durations, firstName, lastName, fullAddress, startTime, durations) {
    var clicked = false;
    var infowindow = new google.maps.InfoWindow()
    if (distances.length != markers.length - 1)
        return;

    var bounds = new google.maps.LatLngBounds();
    for (var i=0;i<markers.length;i++) {
		//var marker, mymarker;
        var marker;
        if (lastName[i] == null)
            lastName[i] = "";
        if (firstName[i] == null)
            firstName[i] = "";     
        var contentString = '<div><b>' + firstName[i] + ' ' + lastName[i] + '</b></div>' + '<div><b>' + fullAddress[i] + '</b></div>' + '<div><b>' + startTime[i] + '</b></div>'
        if (i > 0) {
            WholeContent += '<div>Location <b>' + i + "</b> </div>" + "<div>Name : " + '<b>' + firstName[i - 1] + ' ' + lastName[i - 1] + '</b></div>' + '<div>Address : <b>' + fullAddress[i - 1] + '</b></div>' + '<div>StartTime : <b>' + startTime[i - 1] + '</b></div>' + "<div>Driving Distance/ETA to next Location : <font color='green'><b>" + distances[i - 1] + "</b> / " + "<b>" + durations[i - 1] + "</b></font></div>";
            contentString += "<b>Driving Distance/ETA :</b> " + " <b> " + distances[i - 1] + "</b> / " + "<b>" + durations[i - 1] + "</b>";

        }
        if (i == markers.length - 1) {
            WholeContent += '<div>Location <b>' + (markers.length) + "</b> </div>" + "<div>Name : <b>" + firstName[i] + ' ' + lastName[i] + '</b></div>' + '<div>Address : <b>' + fullAddress[i] + '</b></div>' + '<div>StartTime : <b>' + startTime[i] + '</b></div>';
        }
        marker = new google.maps.Marker({
            position: markers[i].position,
            map: map,
            icon: markers[i].icon
        });
        bounds.extend(marker.getPosition());
    
        google.maps.event.addListener(marker, 'mouseover', (function (marker, contentString, infowindow) {
            return function () {
                if (!clicked) {
                    infowindow.setContent(contentString);
                    infowindow.open(map, marker);
                }
            };
        })(marker, contentString, infowindow));

        google.maps.event.addListener(marker, 'mouseout', (function (marker, contentString, infowindow) {
            return function () {
                if (!clicked) {
                    infowindow.close();
                }
            };
        })(marker, contentString, infowindow));
 
        google.maps.event.addListener(marker, 'click', function () {
        clicked = true;
        infowindow.setContent(WholeContent);
        infowindow.open(map, marker);
        });

        google.maps.event.addListener(infowindow, 'closeclick', function () {
            clicked = false;
        })      
    }

    map.fitBounds(bounds);
}


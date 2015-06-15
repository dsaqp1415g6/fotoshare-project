var API_BASE_URL = "http://147.83.7.157:8080/fotoshare-api/fotoshare";

var photoid_now;
var cookies = document.cookie;
var username = getCookie("username");
var password = getCookie("password");

var loginlogout; 

function addUsername()
{
	 
	if(getCookie('username')=="")
	{
		document.getElementById('username_login').innerHTML = "<b>Inicia sessió</b>";
	}
	else{
		document.getElementById('username_login').innerHTML = "<b>"+getCookie('username')+" </b>";
	}
		
}

$("#button_getphotos").click(function(e) {
	e.preventDefault();
	getPhotos();
});

$("#button_login").click(function(e) {
	e.preventDefault();
   
	if(getCookie('username')=="")
	{
		var newLogin = new Object();
		newLogin.username = $("#username").val()
		newLogin.userpass= $("#password").val()
		loginlogout = "1";				 
		Login(newLogin);
	}
	else{
		 $('<div class="alert alert-danger"> <strong>Error!</strong> Abans has de tancar sessió en el compte actual. </div>').appendTo($("#loginform"));
	}
		
});

$("#button_logout").click(function(e) {
	e.preventDefault();
	
	if(getCookie('username')=="") //ha hecho login
	{
		 $('<div class="alert alert-danger"> <strong>Error!</strong> Per a poder tancar sessió has d&#39;haver iniciat sessió... Prova a registrar-te i després inicia sessió!</div>').appendTo($("#loginform"));
	}
	else
	{
		Logout();
		window.location = "index.html";
	}
});

// * * * * *  Registrarse/Login/shareit/searchit * * * * * * * 
$("#button_search").click(function(e) {
    e.preventDefault();
                        
   var titulo_search = $("#search_input").val();                 
   searchIt(titulo_search);
});
$("#button_registrarse").click(function(e) {
    e.preventDefault();
                         
    var newSignin = new Object();
    newSignin.username = $("#username").val();
    newSignin.password = $("#password").val();
	newSignin.name = $("#name").val();
	newSignin.email = $("#email").val();
	valor = document.getElementById("email").value;
	if (document.getElementById("email").value.indexOf('@') == -1) 
	{
		alert('Introdueixi un correu electrònic vàlid, ha oblidat l\'@\'?'');
		return false;	 
	}
	else if (document.getElementById("email").value.indexOf('.') == -1) 
	{
		alert('Introdueix una direcció de correu electrònic vàlida, ha oblidat el punt \'.\'?');
		return false;
	}
	
	Signup(newSignin);
	                        
    
});

$("#button_updateit").click(function(e) {
    e.preventDefault();
	//$("#photos_result").text(' ');
	$("#fotoreviews_result").text(' ');
	document.getElementById("newReviewForm").style.visibility="hidden";
    document.getElementById("formeditar").style.visibility="visible";   

	if(getCookie('username')=="") 
	{
		$('<div class="alert alert-danger"> <strong>Error!</strong> Abans has d&#39;iniciar sessió</div>').appendTo($("#fotoreviews_result"));
	}
	else
	{
		var newUpdatePhoto = new Object();
		newUpdatePhoto.username = getCookie('username');
		newUpdatePhoto.photoname = $("#photoname").val()
		newUpdatePhoto.category = $("#category").val()
		newUpdatePhoto.photoid=photoid_now;
    
		updatePhoto(newUpdatePhoto.photoid);
	}					 
   	
   
  
    updatePhoto(newUpdatePhoto, photoid_now);
});
$("#button_photo").click(function(e) {
    e.preventDefault();
                         
    var newPhoto = new Object();
    newPhoto.username = $("#username").val()
    newPhoto.photoname = $("#photoname").val()
    newPhoto.category = $("#category").val()
    
  
    Signin(newSignin);
});
$("#button_postreview").click(function(e) {
    e.preventDefault();
                         
	if(getCookie('username')=="")
	{
		$('<div class="alert alert-danger"> <strong>Error!</strong> Abans has d\'iniciar sessió</div>').appendTo($("#fotoreviews_result"));
	}
	else
	{
		var newReview = new Object();
		newReview.username = getCookie('username');
		newReview.reviewtext= $("#textA").val();
		newReview.photoid=photoid_now;
		newReview.date = "15-6-2015";
	  
		postReview(newReview, newReview.photoid);
	}					 
   
	
});
// * * * * *  hasta aquí * * * * * * * 

// * * * * *  Botones de ordenar por --- * * * * * * * 
$("#button_score").click(function(e) {
	e.preventDefault();  
	$("#foto_result").text(' ');
	var ordenarpor = "byscore";
	 getPhotosByX(ordenarpor);
});

$("#button_date").click(function(e) {
	e.preventDefault();  
	$("#foto_result").text(' ');
	 var ordenarpor = "bydate";
	 getPhotosByX(ordenarpor);
});

$("#button_username").click(function(e) {
	e.preventDefault();  
	$("#foto_result").text(' ');
	 var ordenarpor = "byusername";
	 getPhotosByX(ordenarpor);
});


function getPhotos(){
    var url = "http://147.83.7.157:8080/fotoshare-api/fotoshare";
	$("#foto_result").text(' ');
	
	$.ajax({
           url : url,
           type : 'GET',
           dataType : 'json',
           crossDomain : true,
           
    }).done(function(data, status, jqxhr) {
        var photos = data;
        
            $.each(photos.photo, function(i, v){
                   var photo = v;
				    
					document.getElementById("newReviewForm").style.visibility="hidden";
					document.getElementById("formeditar").style.visibility="hidden";
					
					if(getCookie('username')=="")
					{
						document.getElementById('username_login').innerHTML = "<b>Inicia sessió!</b>";
					}
					else	
					{
						document.getElementById('username_login').innerHTML = "<b>"+getCookie('username')+" </b>";
					}					
					
				   $('<form class="form-horizontal" style="background-color:#F0EEEA" role="form">').appendTo($('#foto_result'));
				   $('<div class="form-group" style="background-color:#F0EEEA">').appendTo($('#foto_result'));
                   $('<h4><b> Nom de la foto: ' + photo.photoname+ '</b></h4>').appendTo($('#foto_result'));
                   $('<h5> Nom d\'usuari: ' + photo.username + '</h5>').appendTo($('#foto_result'));
                   $('<h5> Data: ' + photo.fecha + '</h5>').appendTo($('#foto_result'));
				   $('<h5> URL: ' + photo.url+ '</h5>').appendTo($('#foto_result'));
				   
				    //añadimos para reproducir el vídeo en html el siguiente código:
				    $(' <div class="col-sm-12"><img id ="demo' + photo.photoid +'" src="'+ photo.url+'" type="image/jpeg"></div>').appendTo($('#foto_result'));
               
                   var category = photo.categories[0];

                   $('<strong> Categoria: </strong> ' + category.category + '<br>').appendTo($('#foto_result'));
                   
                   
                   
                   var score = photo.score;
				  
                   $('<strong> Puntuació: </strong> ' + score + '<br>').appendTo($('#foto_result'));
                   
                   $('<button type="button" class="btn success" id="button_photo" style="color:white;background-color:#3E3E3E;width:90px; height:4" onClick="getPhotoById('+photo.photoid+')">Photo ' + photo.photoid + '</button>').appendTo($('#foto_result'));
				   
                   
				   
				   $('</div>').appendTo($('#foto_result'));
				   $('</form>').appendTo($('#foto_result'));
				   $('<div class ="col-sm-12" style="padding: 3px 10px 1px 10px;" ><center><legend></legend></center><br></div></div>').appendTo($('#foto_result'));
				   
            });
            
          
            
    }).fail(function() {
		$("#foto_result").text("");
		$('<div class="alert alert-danger">No s\'han pogut carregar les imatges </div>').appendTo($("#foto_result"));
    });
}
function getPhotoById(photoid)
{
	var url = "http://147.83.7.157:8080/fotoshare-api/fotoshare/"+photoid;
	$("#foto_result").text(' ');
	$("#fotoreviews_result").text(' ');
	photoid_now = photoid;
	$.ajax({
           url : url,
           type : 'GET',
           crossDomain : true,
           
    }).done(function(data, status, jqxhr) {
        var photos = data;
        
        if(photos.photoname!=null)
		{		

			if(getCookie('username')=="")
			{
				document.getElementById('username_login').innerHTML = "<b>Inicia Sessió!</b>";
			}
			else	
			{
				document.getElementById('username_login').innerHTML = "<b>"+getCookie('username')+" </b>";
			}					
			
					$('<center><div class ="col-sm-12" style="padding: 10px 10px 11px 0px;" ><h3><b>' + photo.photoname+ '</b></h3></div></center>').appendTo($('#foto_result'));
							 //añadimos para reproducir el vídeo en html el siguiente código:
				    $(' <div class="col-sm-12"><img id ="demo" src="'+ photos.url+'" type="image/jpg"></div></div>').appendTo($('#foto_result'));
               
			   
					$('<div class ="col-sm-12" style="padding: 10px 10px 15px 110px;" ><div class="btn-toolbar" role="toolbar"> <div class="btn-group"><div class=class ="col-sm-8"><div class="btn-group"><button type="button" style="color:white;background-color:#3E3E3E;width:110px; height:4" class="btn dropdown-toggle"data-toggle="dropdown">Puntuació <span class="caret"></span></button><ul class="dropdown-menu"><li><a onClick="postScore('+photos.photoid+',1)">1</a></li><li><a onClick="postScore('+photos.photoid+',2)">2</a></li><li><a onClick="postScore('+photos.photoid+',3)">3</a></li><li><a onClick="postScore('+photos.photoid+',4)">4</a></li><li><a onClick="postScore('+photos.photoid+',5)">5</a></li><li><a onClick="postScore('+photos.photoid+',6)">6</a></li><li><a onClick="postScore('+photos.photoid+',7)">7</a></li><li><a onClick="postScore('+photos.photoid+',8)">8</a></li><li><a onClick="postScore('+photos.photoid+',9)">9</a></li><li><a onClick="postScore('+photos.
photoid+',10)">10</a></li></ul></div><button type="button" class="btn success" id="button_postreview" style="color:white;background-color:#CB2626;width:90px; height:4" onClick="deletePhoto('+photos.photoid+')">El·liminar</button></div></div></div></div>').appendTo($('#foto_result'));
					
					
					$('<h5><strong>Nom d\'usuari: ' + photos.username + '</strong></h5>').appendTo($('#foto_result'));
					$('<h5> <strong>Data: ' + photos.date+ '</strong></h5>').appendTo($('#foto_result'));
					   
				
                   var category = photos.categories[0];
                   $('<strong> Categoria: </strong> ' + category.category + '<br>').appendTo($('#foto_result'));
                   
				   
                   var scores = photos.scores[0];
				   if(scores==null)
				   {
						$('<strong> Puntuació: </strong> No hi ha puntuació registrada <br>').appendTo($('#foto_result'));
				   }
				   else
				   {
						$('<strong> Puntuació: </strong> ' + scores.score + '<br></div>').appendTo($('#foto_result'));
					}
					
					$('<center><legend> Comentaris </legend></center><br></div>').appendTo($('#fotoreviews_result'));
					document.getElementById("newReviewForm").style.visibility="visible";
				   var reviews = photos.reviews[0];
				   if (reviews == null)
				   {
						$('<strong><h5> Comentaris: </strong></h5> No hi ha comentaris. <br>').appendTo($('#fotoreviews_result'));
				   }
				   else
				   {
					document.getElementById("newReviewForm").style.visibility="visible";
				    $.each(photos.reviews, function(i, r)
						{
							var rev=r;
							
						   $('<div> <div class ="col-sm-12" style="padding: 3px 10px 1px 10px;" ><h5> <strong> ' + rev.username + '</strong></h5><h5> '+ rev.reviewtext + '</h5></div>').appendTo($('#fotoreviews_result'));
						   $('<div class ="col-sm-12"> <button type="button" class="btn success" id="button_deleteReview" style="color:white;background-color:red;width:160px; height:4" onClick="deleteReview('+rev.reviewid+', '+photos.photoid+')">El·liminar comentari</button> </div><legend></legend> </div>').appendTo($('#fotoreviews_result'));
						   
		                   
					   });
				   }
		}
		else
		{
			$('<div class="alert alert-danger"> <strong>Oh!</strong> No s\'ha pogut carregar la imatge </div>').appendTo($("#foto_result"));
		}
				   	  
    }).fail(function() {
		$("#foto_result").text("Error");
		$('<div class="alert alert-danger"> No s\'ha pogut carregar la llista d\'imatges </div>').appendTo($("#foto_result"));
    });

}

function getPhotosByX(ordenarpor)
{
    var url = "http://147.83.7.157:8080/fotoshare-api/fotoshare/" + ordenarpor;
	$("#foto_result").text(' ');
	
	$.ajax({
           url : url,
           type : 'GET',
           dataType : 'json',
           crossDomain : true,
           
	}).done(function(data, status, jqxhr) {
        var photos = data;
        
            $.each(photos.photo, function(i, v){
                   var photo = v;
				    
					document.getElementById("newReviewForm").style.visibility="hidden";
					document.getElementById("formeditar").style.visibility="hidden";
					
					if(getCookie('username')=="")
					{
						document.getElementById('username_login').innerHTML = "<b>Inicia Sessió!</b>";
					}
					else	
					{
						document.getElementById('username_login').innerHTML = "<b>"+getCookie('username')+" </b>";
					}					
					
				   $('<form class="form-horizontal" style="background-color:#F0EEEA" role="form">').appendTo($('#foto_result'));
				   $('<div class="form-group" style="background-color:#F0EEEA">').appendTo($('#foto_result'));
                   $('<h4><b> Nom de la foto: ' + photo.photoname+ '</b></h4>').appendTo($('#foto_result'));
                   $('<h5> Nom d\'usuari: ' + photo.username + '</h5>').appendTo($('#foto_result'));
                   $('<h5> Fecha: ' + photo.date+ '</h5>').appendTo($('#foto_result'));
				   $('<h5> URL: ' + photo.url+ '</h5>').appendTo($('#foto_result'));
				   
				    //añadimos para reproducir el vídeo en html el siguiente código:
				    $(' <div class="col-sm-12"><img id ="demo' + photo.photoid +'" src="'+ photo.url+'" type="image/jpg"></div>').appendTo($('#foto_result'));
               
                   var category = photo.categories[0];

                   $('<strong> Categoria: </strong> ' + category.category + '<br>').appendTo($('#foto_result'));
                   
                   
                   
                   var score = photo.score;
				  
                   $('<strong> Puntuació: </strong> ' + score + '<br>').appendTo($('#foto_result'));
                   
                   $('<button type="button" class="btn success" id="button_photo" style="color:white;background-color:#3E3E3E;width:90px; height:4" onClick="getPhotoById('+photo.photoid+')">Photo ' + photo.photoid + '</button>').appendTo($('#foto_result'));
				   
                   
				   
				   $('</div>').appendTo($('#foto_result'));
				   $('</form>').appendTo($('#foto_result'));
				   $('<div class ="col-sm-12" style="padding: 3px 10px 1px 10px;" ><center><legend></legend></center><br></div></div>').appendTo($('#foto_result'));
				   
            });
            
          
            
    }).fail(function() {
		$("#foto_result").text("Error");
		$('<div class="alert alert-danger">No s\'ha pogut carregar la llista de fotos </div>').appendTo($("#foto_result"));
    });
}

function buscarPorCategoria(category)
{
    var url = "http://147.83.7.157:8080/fotoshare-api/fotoshare/searchc?category="+category;
	$("#foto_result").text(' ');
	
	$.ajax({
           url : url,
           type : 'GET',
           crossDomain : true,
           
	}).done(function(data, status, jqxhr) {
        var photos = data;
        
            $.each(photos.photo, function(i, v){
                   var photo = v;
				   
					document.getElementById("newReviewForm").style.visibility="hidden";
					document.getElementById("formeditar").style.visibility="hidden";
					
					if(getCookie('username')=="")
					{
						document.getElementById('username_login').innerHTML = "<FONT FACE=\"impact\" SIZE=6 COLOR=\"black\">Inicia sessió!</FONT>";
					}
					else	
					{
						document.getElementById('username_login').innerHTML = "<FONT FACE=\"impact\" SIZE=6 COLOR=\"black\">"+getCookie('username')+" </FONT>";
					}					
					
				   $('<form class="form-horizontal" style="background-color:#F0EEEA" role="form">').appendTo($('#foto_result'));
				   $('<div class="form-group" style="background-color:#F0EEEA">').appendTo($('#foto_result'));
                   $('<h4><b> Nom de la foto: ' + photo.photoname+ '</b></h4>').appendTo($('#foto_result'));
                   $('<h5> Nom d\'usuari: ' + photo.username + '</h5>').appendTo($('#foto_result'));
                   $('<h5> Data: ' + photo.date+ '</h5>').appendTo($('#foto_result'));
				   $('<h5> URL: ' + photo.url+ '</h5>').appendTo($('#foto_result'));
				   
				    //añadimos para reproducir el vídeo en html el siguiente código:
				    $(' <div class="col-sm-12"><img id ="demo' + photo.photoid +'" src="'+ photo.url+'" type="image/jpg"></div>').appendTo($('#foto_result'));
               
                   var category = photo.categories[0];

                   $('<strong> Categoria: </strong> ' + category.category + '<br>').appendTo($('#foto_result'));
                   
                   
                   
                   var score = photo.score;
				  
                   $('<strong> Puntuació: </strong> ' + score + '<br>').appendTo($('#foto_result'));
                   
                   $('<button type="button" class="btn success" id="button_photo" style="color:white;background-color:#3E3E3E;width:90px; height:4" onClick="getPhotoById('+photo.photoid+')">Foto ' + photo.photoid + '</button>').appendTo($('#foto_result'));
				   
                   
				   
				   $('</div>').appendTo($('#foto_result'));
				   $('</form>').appendTo($('#foto_result'));
				   $('<div class ="col-sm-12" style="padding: 3px 10px 1px 10px;" ><center><legend></legend></center><br></div></div>').appendTo($('#foto_result'));
				   
            });
		
    }).fail(function() {
		$('<div class="alert alert-danger">Error amb el servidor </div>').appendTo($("#foto_result"));
    });
}

function Login(newLogin) {
    var username = newLogin.username;
    var password = newLogin.password;
	var data = JSON.stringify(newLogin);
	var url = API_BASE_URL +"/login";
	$("#foto_result").text('');
	
		//mandamos a la API la user+password y comprobamos que sea correcto
		$.ajax({
			url : url,
			type : 'POST',
			crossDomain : true,
			dataType : 'json',
			data : data,
			cache : false,
			contentType : "application/vnd.fotoshare.api.user+json; charset=UTF-8 ",
			processData : false
           
           }).done(function(data, status, jqxhr) {
               
        	   
        	   $('<div class="alert alert-success"> <strong>Benvingut a Fotoshare! </strong></div>').appendTo($("#loginform"));
			
				setCookie('username', username, 1 );
				var usernamec = getCookie('username');
				
				document.getElementById('username_login').innerHTML = "<FONT FACE=\"impact\" SIZE=6 COLOR=\"black\">"+getCookie('username')+" </FONT>";
				//redirigimos la página principal:
				window.location = "perfil.html";
				
            }).fail(function() {
                    $('<div class="alert alert-danger">Comprova que hagis intoduït correctament l\'usuari i la contrasenya.</div>').appendTo($("#loginform"));
            });

}
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toGMTString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}
function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i].trim();
		if (c.indexOf(name) == 0)
			return c.substring(name.length, c.length);
	}
	return "";
}

function Logout()
{
	//eliminamos la cookie
	document.cookie = 'username=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	document.getElementById('username_login').innerHTML = "<FONT FACE=\"impact\" SIZE=6 COLOR=\"black\">Inicia sessió</FONT>";
					
}

function deletePhoto(photoid)
{
	
	if(getCookie('username')=="")
	{
		$('<div class="alert alert-danger">Abans has d\'iniciar sessió</div>').appendTo($("#foto_result"));
	}
	else
	{
			
	var username_to_delete = getCookie('username');
	var url = API_BASE_URL + "/" + photoid + "/" + username_to_delete; 
	$("#foto_result").text(' ');
    
	
	var data = JSON.stringify(username_to_delete);
		//mandamos a la API la user+password y comprobamos que sea correcto
		$.ajax({
			url : url,
			type : 'DELETE',
			crossDomain : true,
			dataType : 'json',
			data : data,
			cache : false,
			contentType : false,
			processData : false
           
           }).done(function(data, status, jqxhr) {
				window.location = "index.html";	
            }).fail(function() {
                window.location = "index.html";   
            });
	}
}
function deleteReview(reviewid, photoid)
{
	if(getCookie('username')=="")
	{
		$('<div class="alert alert-danger">Abans has d\'iniciar sessió</div>').appendTo($("#foto_result"));
	}
	else
	{
		
	var username_to_delete = getCookie('username');
	var url = API_BASE_URL+"/"+ photoid + "/reviews/" + reviewid + "/"+ username_to_delete; 
	$("#foto_result").text(' ');
    
	
		$.ajax({
			url : url,
			type : 'DELETE',
			crossDomain : true
           
           }).done(function(data, status, jqxhr) {
        	   $("#fotoreviews_result").text(' ');
			   getPhotoById(photoid);
            }).fail(function() {
            	$("#fotoreviews_result").text(' ');
				   getPhotoById(photoid);  
            });
	}
}

function Signup(newSignup) {
	var url = API_BASE_URL+"/users"; 
	$("#foto_result").text(' ');
    var data = JSON.stringify(newSignup);


		
		$.ajax({
			url : url,
			type : 'POST',
			crossDomain : true,
			dataType : 'json',
			data : data,
			cache : false,
			contentType : "application/vnd.fotoshare.api.user+json; charset=UTF-8 ",
			processData : false
           
           }).done(function(data, status, jqxhr) {
                   $('<div class="alert alert-success"> Benvingut. Has iniciat sessió.</div>').appendTo($("#loginform"));
				   window.location = "login.html";
            }).fail(function() {
                    $('<div class="alert alert-danger"> Error </div>').appendTo($("#loginform"));
            });

	  }

function postReview(newReview,photoid) {
	
	var url = API_BASE_URL +"/"+photoid+ "/reviews"; //falta añadir la URL para comprobar el usuario
	$("#foto_result").text(' ');
	
	var data = JSON.stringify(newReview);
		//mandamos a la API la user+password y comprobamos que sea correcto
		$.ajax({
			url : url,
			type : 'POST',
			crossDomain : true,
			dataType : 'json',
			data : data,
			cache : false,
			contentType : "application/vnd.fotoshare.api.reviews+json; charset=UTF-8 ",
			processData : false
           
           }).done(function(data, status, jqxhr) {
                   //$('<div class="alert alert-success"> <strong> Comentario añadido </strong></div>').appendTo($("#foto_result"));
				   $("#fotoreviews_result").text(' ');
				   getPhotoById(photoid);
				   
            }).fail(function() {
                    $('<div class="alert alert-danger"> Error </div>').appendTo($("#foto_result"));
            });

	  }
	  
function postScore(photoid, score) {
var url = API_BASE_URL +"/"+ photoid + "/score"; //falta añadir la URL para comprobar el usuario
	$("#foto_result").text(' ');
   
	var newScore = new Object();
	newScore.username = getCookie('username');
	newScore.score= score; 
	newScore.photoid=photoid;

	var data = JSON.stringify(newScore);
		//mandamos a la API la user+password y comprobamos que sea correcto
		$.ajax({
            url : url,
			type : 'POST',
			crossDomain : true,
			dataType : 'json',
			data : data,
			cache : false,
			contentType : "application/vnd.fotoshare.api.score+json; charset=UTF-8 ",
			processData : false
           
           }).done(function(data, status, jqxhr) {
                   $('<div class="alert alert-success">Puntuació feta.</div>').appendTo($("#foto_result"));
				   getPhotoById(photoid);
            }).fail(function() {
                    $('<div class="alert alert-danger">Ja l\'has puntuat!').appendTo($("#foto_result"));
            });
}

function progressHandlingFunction(e) {
	if (e.lengthComputable) {
		$('progress').attr({
			value : e.loaded,
			max : e.total
		});
	}
}

function updatePhoto(photoid) {
	
		var url = API_BASE_URL + "/"+ photoid;
	
		var newUpdatePhoto = new Object();
		newUpdatePhoto.username = getCookie('username');
		newUpdatePhoto.photoname = $("#photoname").val()
		newUpdatePhoto.category = $("#category").val()
		newUpdatePhoto.photoid=photoid_now;
    
	
		var data = JSON.stringify(newUpdatePhoto);
		
		$.ajax({
			   url : url,
				type : 'PUT',
				crossDomain : true,
				dataType : 'json',
				data : data,
				cache : false,
				contentType : "application/vnd.fotoshare.api.photos+json; charset=UTF-8 ",
				processData : false
			   
			   }).done(function(data, status, jqxhr) {
					window.location = "index.html";	
				}).fail(function() {
					window.location = "index.html";	
				});
			
	
    

}

function searchIt(titulo)
{
 var url = "http://147.83.7.157:8080/fotoshare-api/fotoshare/search?titulo="+titulo;
	$("#foto_result").text(' ');
	
	$.ajax({
           url : url,
           type : 'GET',
           crossDomain : true,
           
    }).done(function(data, status, jqxhr) {
        var photos = data;
        
		
            $.each(photos.photo, function(i, v){
                var photo = v;
				
					document.getElementById("newReviewForm").style.visibility="hidden";
				   $('<form class="form-horizontal" style="background-color:#F0EEEA" role="form">').appendTo($('#foto_result'));
				   $('<div class="form-group" style="background-color:#F0EEEA">').appendTo($('#foto_result'));
                   $('<h4><b> Nom de la foto: ' + photo.photoname+ '</b></h4>').appendTo($('#foto_result'));
                   $('<h5> Nom d\'usuari: ' + photo.username + '</h5>').appendTo($('#foto_result'));
                   $('<h5> Data: ' + photo.fecha+ '</h5>').appendTo($('#foto_result'));
				   $('<h5> URL: ' + photo.url+ '</h5>').appendTo($('#foto_result'));
				   
				    //añadimos para reproducir el vídeo en html el siguiente código:
				    $(' <div class="col-sm-12"><img id ="demo' + photo.photoid +'" src="'+ photo.url+'" type="image/jpg"></div>').appendTo($('#foto_result'));
               
                   var category = photo.categories[0];

                   $('<strong> Categoria: </strong> ' + category.category + '<br>').appendTo($('#foto_result'));
                   $('<button type="button" class="btn success" id="button_photo" style="color:white;background-color:#3E3E3E;width:90px; height:4" onClick="getPhotoById('+photo.photoid+')">Foto ' + photo.photoid + '</button>').appendTo($('#foto_result'));
				   
				   $('</div>').appendTo($('#foto_result'));
				   $('</form>').appendTo($('#foto_result'));
				   $('<div class ="col-sm-12" style="padding: 3px 10px 1px 10px;" ><center><legend></legend></center><br></div></div>').appendTo($('#foto_result'));
				
			});
    }).fail(function() {
		$('<div class="alert alert-danger">No hi ha vídeos amb aquest títol. </div>').appendTo($("#foto_result"));
    });
}



$('form#photoForm').submit(
		function(e) {
			e.preventDefault();
			$('progress').toggle();

			var formData = new FormData($('form#photoForm')[0] );
			var URL = API_BASE_URL + "/upload/" + getCookie('username');
			$.ajax(
					{
						url : URL,
						type : 'POST',
						xhr : function() {
							var myXhr = $.ajaxSettings.xhr();
							if (myXhr.upload) {
								myXhr.upload.addEventListener('progress',
										progressHandlingFunction, false); // upload
							}
							return myXhr;
						},
						crossDomain : true,
						data : formData,
						cache : false,
						// mimeType:"multipart/form-data",
						contentType : false,
						processData : false

					}).done(function(data, status, jqxhr) {
				var response = $.parseJSON(jqxhr.responseText);
				lastFilename = response.filename;
				$('#sharephoto').attr('src', response.photoURL);
				$('progress').toggle();
				$('form')[0].reset();
				window.location = "index.html";
			}).fail(function(jqXHR, textStatus) {
				alert("Error");
				console.log(textStatus);
			});
		});

function progressHandlingFunction(e) {
	if (e.lengthComputable) {
		$('progress').attr({
			value : e.loaded,
			max : e.total
		});
	}
}
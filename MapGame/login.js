function login(){
	var form=document.getElementById("login");
	form.elements[1].value=CryptoJS.SHA256(form.elements[1].value).toString(CryptoJS.enc.Hex);
	if (typeof form.elements[2].value != 'undefined')
	{
		if (form.elements[2].value != "Submit") 
		{
			form.elements[2].value=CryptoJS.SHA256(form.elements[2].value).toString(CryptoJS.enc.Hex);
			if (form.elements[1].value != form.elements[2].value)
			{
				alert("Passwords do not match!")
			}
		}
	}
}
function login2(){
	var form=document.getElementById("login");
	form.elements[0].value=CryptoJS.SHA256(form.elements[0].value).toString(CryptoJS.enc.Hex);
	form.elements[1].value=CryptoJS.SHA256(form.elements[1].value).toString(CryptoJS.enc.Hex);
	if (form.elements[0].value != form.elements[1].value)
	{
		alert("Passwords do not match!")
	}
}
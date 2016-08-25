var c = document.getElementById("game");
var ctx = c.getContext("2d");

var letters = [' ','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']

var mapImage = new Image();
mapImage.onload = function() {
    ctx.drawImage(mapImage, 0, 0);
};
mapImage.src = 'TestMap.png';

//console.log("test");
drawTroopData();
//console.log("test");

var mode = "rest";
var selectedCountry = "none";

function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: Math.ceil((evt.clientX-rect.left)/(rect.right-rect.left)*canvas.width),
        y: Math.ceil((evt.clientY-rect.top)/(rect.bottom-rect.top)*canvas.height)
    };
}

game.addEventListener('mousedown', function(evt) {
    var mousePos = getMousePos(game, evt);
    var mouseColor = ctx.getImageData(mousePos.x, mousePos.y, 1, 1).data;
    console.log('Mouse position: ' + mousePos.x + ',' + mousePos.y);
    console.log('Color: ' + mouseColor);
    switch(mode) {
        case "chooseCapital":
            chooseCapital(mouseColor[3]);
            mode = "rest";
            break;
        case "trainPrimed":
            trainTroops(mouseColor[3]);
            mode = "rest";
            break;
        case "attackPrimed":
            selectedCountry=mouseColor[3];
            mode = "attackTarget";
            break;
        case "attackTarget":
            attack(mouseColor[3]);
            mode="rest";
            selectedCountry="none";
            break;
        case "buildNavy":
            buildNavy(mouseColor[3]);
            mode="rest"
            break;
    }

}, false);

function chooseCapitalPrimer()
{
    mode = "chooseCapital";
}

function chooseCapital(target)
{
    var ajax = getAjax();
    ajax.open('POST', 'gameFunctions.php', true);
    ajax.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    ajax.onreadystatechange = function() {
        if(ajax.readyState==4) {
            if(ajax.responseText.length>0) {
                alert(ajax.responseText);
            }
            location.reload(true);
        }
    }
    ajax.send("request=chooseCapital&choice="+target);
}

function attackPrimer()
{
    mode = "attackPrimed";
}

function attack(target)
{
    var ajax = getAjax();
    ajax.open('POST', 'gameFunctions.php', true);
    ajax.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    ajax.onreadystatechange = function() {
        //console.log(ajax.readyState);
        if(ajax.readyState==4) {
            if(ajax.responseText.length>0) {
                console.log(ajax.responseText);
            }
            location.reload(true);
        }
    }
    ajax.send("request=attack&origin="+selectedCountry+"&target="+target+"&strength="+document.getElementById("attack").value);
}

function trainPrimer()
{
    mode = "trainPrimed";
}

function trainTroops(target)
{
    var ajax = getAjax();
    ajax.open('POST', 'gameFunctions.php', true);
    ajax.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    ajax.onreadystatechange = function() {
        //console.log(ajax.readyState);
        if(ajax.readyState==4) {
            if(ajax.responseText.length>0) {
                alert(ajax.responseText);
            }
            location.reload(true);
        }
    }
    ajax.send("request=trainTroops&origin="+target);
}

function buildNavyPrimer()
{
    mode="buildNavy";
}

function buildNavy(target)
{
    
}

function removeEveryNth(array, n)
{
    var newArray = new Array();
    var newArrayIndex = 0;
    for (var i = 0; i <        array.length; i++) {
        if ((i+1)%n==0) {
            i++;
        }
        if(i<array.length)
        {
            newArray[newArrayIndex]=array[i];
            newArrayIndex++;
        }
    };
    return newArray;
}

function drawTroopData()
{
    //console.log("test");
    var ajax=getAjax();
    var troops;
    ajax.open('POST', 'gameFunctions.php', true);
    ajax.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    ajax.onreadystatechange = function() {
        //console.log(ajax.readyState);
        if(ajax.readyState==4) {
            //console.log(ajax.responseText);
            troops = ajax.responseText; //Array elements of structure [[count1,count2,...],[[ocolor1r,ocolor1g,ogolor1b],[ocolor2r,ocolor2g,ocolor2b],...],[coordx,coordy]],...
            drawTroopData2(troops);
        }
    }
    ajax.send("request=troopData");
}

function drawTroopData2(troopJSON)
{
    if (typeof troopJSON == "") {
        return;
    }
    console.log(troopJSON);
    troops=JSON.parse(troopJSON);
    ctx.clearRect(0, 0, c.width, c.height);
    ctx.drawImage(mapImage, 0, 0);
    var troopcounts = [];
    var ownercolors = [];
    var coords = [];
    //console.log("test");
    for (var i = 0; i < troops.length; i++) {
        troopcounts[i]=troops[i][0];
        ownercolors[i]=troops[i][1];
        coords[i]=troops[i][2];
    };
    for (var h =0; h < troopcounts.length; h++) {
        for (var i = 0; i < troopcounts[h].length; i++) {
            ctx.fillStyle=rgbToHex(ownercolors[h][i].split(","));
            ctx.textAlign="center";
            ctx.font = "16px Arial";
            if(troopcounts[h][i]!=0) {
                ctx.fillText(troopcounts[h][i], coords[h][0], coords[h][1]+i*16);
            }
        };
    };
}

function endTurn()
{
    var ajax = getAjax();
    ajax.open('POST', 'gameFunctions.php', true);
    ajax.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    ajax.onreadystatechange = function() {
        if(ajax.readyState==4) {
            console.log(ajax.responseText);
            endTurn2(ajax.responseText);
        }
    }
    ajax.send("request=getActions");
    return;
}

function endTurn2(actions)
{
    if(actions>0) {
        if(!confirm("You still have actions. Are you sure you want to end your turn?"))
        {
            return;
        }
    }
    var ajax = getAjax();
    ajax.open('POST', 'gameFunctions.php', true);
    ajax.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    ajax.onreadystatechange = function() {
        if(ajax.readyState==4) {
            console.log(ajax.responseText);
            location.reload(true);
        }
    }
    ajax.send("request=endTurn");
    return;
}

function getAjax()
{
    var xmlHttpReq = false;             
    if (window.XMLHttpRequest) {
        ajax = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
        ajax = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return ajax;
}

function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(rgb) {
    //console.log(rgb);
    return "#" + componentToHex(parseInt(rgb[0])) + componentToHex(parseInt(rgb[1])) + componentToHex(parseInt(rgb[2]));
}
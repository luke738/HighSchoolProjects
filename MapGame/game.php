<?php
session_start();
if($_SESSION["validUser"]<1)
{
    echo "<script type='text/javascript'>alert(\"Please log in!\\nYou will be redirected to the login page.\");window.location=\"index.php\"</script>";
}
$_SESSION["saveFile"]=simplexml_load_file("save.xml")->asXML();
?>
<html>
<head>
    <title>Map Game</title>
    <script type="text/javascript" src="mapGame.js" defer></script>
</head>
<body>
	<h1>Map Game</h1>
    Welcome <?php echo $_SESSION["username"] ?>!
    <?php
        $save=simplexml_load_string($_SESSION["saveFile"]);
        $users=$save->players;
        $user=$users->player;
        $mod=false;
        foreach ($user as $u) {
            if (strcmp((string)$u->username, $_SESSION["username"])==0 && strcmp((string)$u['type'], "moderator")==0) {
                $mod=true;
                $_SESSION["userSave"]=$u->asXML();
            }
            elseif (strcmp((string)$u->username, $_SESSION["username"])==0) {
                $_SESSION["userSave"]=$u->asXML();
            }
        }
        if ($mod) {
            $_SESSION["validUser"]=2;
            echo "<h2>Mod Controls:</h2>";
            if($save->round<=0){
                echo "<form id=\"modToolsPre\">
                        <button type=\"button\" onclick=\"modParse('scrambleTurn')\">Scramble Turn Orders</button><br>
                        <button type=\"button\" onclick=\"modParse('capitalSelection')\">Open Capital Selection</button><br>
                        <button type=\"button\" onclick=\"modParse('startGame')\">Start Game!</button><br>
                        Confirm: <input type=\"checkbox\" name=\"confirm\">
                      </form>";
            }
            echo   "<form id=\"modTools\">
                        <button type=\"button\" onclick=\"modParse('pauseGame')\">Pause/Resume Game</button>
                    </form>";
            echo   "<h3>Info</h3>" . count($user) . " of " . (int)$users->max . " players registered.<br>";
            $capCount=0;
            foreach ($user as $u2) {
                if(isset($u2->capital))
                {
                    $capCount++;
                }            
            }
            echo $capCount . " of " . count($user) . " registered players have selected their capitals.";
        }
        else {
            $_SESSION["validUser"]=1;
        }
    ?>
    <script type='text/javascript'>
    function modParse(request)
    {
        if(!document.forms["modToolsPre"]["confirm"].checked)
        {
            return;
        }
        var xmlHttpReq = false;       
        if (window.XMLHttpRequest) {
            ajax = new XMLHttpRequest();
        }

        else if (window.ActiveXObject) {
            ajax = new ActiveXObject("Microsoft.XMLHTTP");
        }
        ajax.open('POST', 'modTools.php', true);
        ajax.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        ajax.onreadystatechange = function() {
            console.log(ajax.responseText);
        }
        ajax.send("request="+request);
    }
    </script>
	<h2>Game</h2>
	<canvas id="game" width="600" height="400" style="border:0px solid #000000;"></canvas>
    <h3>Controls</h3>
    <form id="controls">
        <?php
        if (strcmp($save->currentplayer, $_SESSION["username"])==0) {
            if ($save->round==0) {
                echo '<button type="button" onclick="chooseCapitalPrimer()">Choose Your Capital</button><br>';
            }
            else if ($save->round>0){
                echo '<button type="button" onclick="attackPrimer()">Attack/Move</button>';
                echo ' Number of troops: <input type="text" id="attack"></input><br>';
                echo '<button type="button" onclick="trainPrimer()">Train Troops</button><br>';
                echo '<button type="button" onclick="endTurn()">End Turn</button>';
            }
            else {
                echo "The game has not yet started";
            }
        }
        else {
            echo "It is not your turn.";
        }
        ?>
    </form>
    <h2>Game Status</h2>
    <?php
        $round = (int)$save->round;
        if ($round==-1) {
            echo "The game has not yet started.<br>";
        }
        elseif ($round==0) {
            if (isset(simplexml_load_string($_SESSION["userSave"])->capital)) {
                echo "Other users are choosing their capitals. You chose: " . (string)simplexml_load_string($_SESSION["userSave"])->capital . "<br>";
            }
            else {
                echo "Capital selection in progress. Please choose a capital on your turn.<br>";
                echo (string)$save->currentplayer . "'s turn.<br>";
            }
        }
        else {
            echo "It is currently round " . (int)$save->round . ".<br>";
            echo (string)$save->currentplayer . "'s turn.<br>";
            if (strcmp($save->currentplayer, $_SESSION["username"])==0) {
                echo "You have " . $save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0]->actions . "/3 actions remaining. <br>";
            }
        }
    ?>
    Money: $<?php echo (int)simplexml_load_string($_SESSION["userSave"])->money; ?><br>
	<h2>Rules</h2>
	<p>These are the rules.</p>
</body>
</html>
<?php
session_start();
if ($_SESSION["validUser"]!=2) {
    echo "Unauthorized mod tool access is prohibited!";
    exit();
}
switch ($_POST["request"]) {
    case "scrambleTurn":
        scrambleTurn();
        break;
    case "capitalSelection":
        capitalSelection();
        break;
    case "startGame":
        startGame();
        break;
    default:
        echo "Nope.";
}

function scrambleTurn()
{
    $save=simplexml_load_string($_SESSION["saveFile"]);
    $users=$save->players;
    $user=$users->player;
    for ($i=0; $i < count($user); $i++) { 
        $rand[$i]=$i+1;
    }
    shuffle($rand);
    $x=0;
    foreach ($user as $u) {
        $u->turnorder=$rand[$x];
        $x++;
    }
    $save->asXML("save.xml");
    $_SESSION["saveFile"]=simplexml_load_file("save.xml")->asXML();
}

function capitalSelection()
{
    $save=simplexml_load_string($_SESSION["saveFile"]);
    $save->round=0;
    $save->currentplayer=$save->xpath("/game/players/player[turnorder='1']")[0]->username;
    $save->asXML("save.xml");
    $_SESSION["saveFile"]=simplexml_load_file("save.xml")->asXML();
}

function startGame()
{

}
?>
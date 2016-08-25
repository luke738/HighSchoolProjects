<?php
session_start();
//echo "test";
if ($_SESSION["validUser"]<1) {
    //echo "Unauthorized gameplay is prohibited!";
    exit();
}
switch ($_POST["request"]) {
	case "chooseCapital":
		chooseCapital();
		break;
    case "troopData":
        troopData();
        break;
    case "getActions":
    	getActions();
    	break;
    case "endTurn":
    	endTurn();
    	break;
    case "attack":
    	attack();
    	break;
    case "trainTroops":
    	trainTroops();
    	break;
    default:
        echo "Nope.";
}

function chooseCapital()
{
	$save=simplexml_load_string($_SESSION["saveFile"]);
	if($save->round!=0) {
		exit();
	}
	//echo $_POST["choice"];
	$choice=$save->xpath("/game/continent/country[@id=" . $_POST["choice"] . "]");
	$neighbors = $choice[0]->neighbors;
	$neighbor = strtok($neighbors, ", ");
	$neighborCountries;
	$i=0;
	while ($neighbor !== false)
	{
		$neighborCountries[$i]=$save->xpath("/game/continent/country[name='" . $neighbor . "']")[0];
		$neighbor = strtok(", ");
		$i++;
	}
	if(isset($choice->owner))
	{
		echo "Someone alreaddy owns that country!";
		exit();
	}
	foreach ($neighborCountries as $n) {
		#echo (string)$n->owner . "test\n";
		if(strcmp((string)$n->owner, "")!=0)
		{
			echo "Too close to another player!";
			exit();
		}
	}
	$choice[0]->owner=$_SESSION["username"];
	$choice[0]->addChild("troops")->addAttribute("id","1");
	$choice[0]->troops[0]->addChild("owner",$_SESSION["username"]);
	$choice[0]->troops[0]->addChild("strength",1000);
	$choice[0]->troops[0]->addChild("type","owner");
	$player=$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']");
	$player[0]->addChild("capital",(string)$choice[0]->name);
	$player[0]->actions=0;
	save($save);
	endTurn();
}

function troopData()
{
	$save=simplexml_load_string($_SESSION["saveFile"]);
	$continent=$save->continent;
	$troopcounts;
	$owners;
	$coords;
	foreach ($continent as $cont) {
		$i=0;
		foreach ($cont->country as $c) {
			$j=0;
			$present=false;
			foreach ($c->troops as $t) {
				if(isset($t->owner)) {
					$troopcounts[$i][$j]=(int)$t->strength;
					$owners[$i][$j]=(string)$t->owner;
					$j++;
					$present=true;
				}
			}
			if ($present==true) {
				$coords[$i][0]=(int)strtok((string)$c->trooppoint, ", ");
				$coords[$i][1]=(int)strtok(", ");
				$i++;
			}
		}
	}
	$response;
	$userColors;
	foreach ($save->players->player as $p) {
		$userColors[(string)$p->username]=$p->color;
		//echo $userColors[(string)$p->username] . "\n";
		//echo (string)$p->username . "\n";
	}
	$ownercolors = array();
	if(!isset($owners)) {
		echo "";
		exit();
	}
	//echo $owners[1][0];
	for ($i=0; $i < count($owners); $i++)
	{
		for ($j=0; $j < count($owners[$i]); $j++) { 
			//echo $owners[$i][$j] . ", ";
			$ownercolors[$i][$j]=(string)$userColors[$owners[$i][$j]];
		}
		//echo"\n";
	}
	for ($i=0; $i < count($troopcounts); $i++) { 
		$response[$i][0]=$troopcounts[$i];
		$response[$i][1]=$ownercolors[$i];
		$response[$i][2]=$coords[$i];
	}
	echo json_encode($response);
}

function getActions()
{
	$save=simplexml_load_string($_SESSION["saveFile"]);
	echo (int)$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0]->actions;
}

function attack()
{
	$origin = $_POST["origin"];
	$target = $_POST["target"];
	$strength = $_POST["strength"];
	$save=simplexml_load_string($_SESSION["saveFile"]);
	$origin = $save->xpath("/game/continent/country[@id=" . $origin . "]");
	$target = $save->xpath("/game/continent/country[@id=" . $target . "]");
	if($strength==0 || !(isset($origin[0])==1 && isset($target[0])==1) || !($strength<$origin[0]->xpath("troops[owner=" . $_SESSION["username"] . "]/strength"))) {
		echo "Invalid Attack!\nCheck your requested strength and origin country.";
		exit();
	}
	$validTarget=false;
	$n=strtok($origin[0]->neighbors,", ");
	while($n !== false) {
		if (strcmp((string)$n, (string)$target[0]->name)==0) {
			$validTarget=true;
		}
		$n=strtok(", ");
	}
	if(!$validTarget) {
		echo "Invalid Attack!\nTarget country does not touch origin country.";
		exit();
	}
	$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0]->strength-=$strength;
	$case="";
	$attacker=$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0];
	if(strcmp((string)$target[0]->owner,$_SESSION["username"])==0) {
		$case="move";
	}
	else if(!isset($target[0]->owner)) {
		$case="attack";
	}
	else if((int)$attacker->alliance==(int)$save->xpath("/game/players/player[username='" . $target[0]->owner . "']")[0]->alliance && $attacker->alliance!=0) {
		$case="allymove";
	}
	else {
		$case="attack";
	}
	switch ($case) {
		case "attack":
			if ((int)$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0]->actions<=0) {
				echo "Invalid attack!\nYou are out of actions.";
				exit();
			}
			$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0]->actions-=1;
			if(count($target[0]->troops)-1<0) {
				$start=0;
			}
			else {
				$start=count($target[0]->troops)-1;
			}
			for ($i=$start; $i >= 0; $i--) { 
				if($target[0]->troops[$i]->strength>$strength) {
					$target[0]->troops[$i]->strength-=$strength;
					if ((int)$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0]->strength==0) {
						$troop=$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0];
						unset($troop);
						#echo "unset";
					}
					#echo $target[0]->troops[$i]->strength;
					save($save);
					exit();
				}
				else if($target[0]->troops[$i]->strength<$strength && $target[0]->troops[$i]->strength>0 && $i>0) {
					$strength-=$target[0]->troops[$i]->strength;
					unset($target[0]->troops[$i]);
					if ($strength=0) {
						if ((int)$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0]->strength==0) {
							$troop=$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0];
							unset($troop);
							#echo "unset";
						}
						save($save);
						exit();
					}
					#echo "TesT";
				}
				else {
					$strength-=$target[0]->troops[$i]->strength;
					if($strength>0) {
						$target[0]->troops[$i]->owner=$_SESSION["username"];
						$target[0]->troops[$i]->strength=$strength;
						$target[0]->troops[$i]->type="owner";
						$target[0]->owner=$_SESSION["username"];
					}
					else {
						unset($target[0]->troops[$i]);
						unset($target[0]->owner);
					}
					if ((int)$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0]->strength==0) {
						$troop=$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0];
						unset($troop);
						#echo "unset";
					}
					save($save);
					exit();
				}
			}
			break;
		case "move":
			if ((int)$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0]->actions<=0 && strcmp($target[0]->owner, $_SESSION["username"])!=0) {
				echo "Invalid attack!\nYou are out of actions.";
				exit();
			}
			$target[0]->troops[0]->strength+=$strength;
			break;
		case "allymove":
			if ((int)$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0]->actions<=0) {
				echo "Invalid attack!\nYou are out of actions.";
				exit();
			}
			for ($i=count($target[0]->troops)-1; $i >= 0; $i--) { 
				if(strcmp((string)$target[0]->troops[$i]->owner, $_SESSION["username"])==0) {
					$target[0]->troops[$i]->strength+=$strength;
					if ((int)$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0]->strength==0) {
						$troop=$origin[0]->xpath("/country/troops[owner='" . $_SESSION["username"] . "']")[0];
						unset($troop);
						#echo "unset";
					}
					save($save);
					#echo $origin[0]->asXML();
					exit();
				}
			}
			$i=count($target[0]->troops);
			$target[0]->troops[$i]->strength=$strength;
			$target[0]->troops[$i]->owner=$_SESSION["username"];
			$target[0]->troops[$i]->type="ally";
			break;
		default:
			echo "nope";
	}
	if ((int)$origin[0]->xpath("troops[owner='" . $_SESSION["username"] . "']")[0]->strength==0) {
		if(strcmp($origin[0]->troops[0]->type, "owner")==0) {
			if (count($origin->troops)>1) {
				$origin[0]->owner=$origin[0]->troops[1]->owner;
				$origin[0]->troops[1]->type="owner";
			}
			else {
				unset($origin[0]->owner);
			}
		}
		unset($origin[0]->troops[0]);
		#echo "unset";
	}
	save($save);
}

function trainTroops()
{
	$origin = $_POST["origin"];
	$save=simplexml_load_string($_SESSION["saveFile"]);
	$origin = $save->xpath("/game/continent/country[@id=" . $origin . "]");
	#echo (string)$origin[0]->owner . " " . $_SESSION["username"];
	if(!isset($origin[0])==1 || strcmp((string)$origin[0]->owner, $_SESSION["username"])!=0) {
		echo "Invalid Training!\nCheck your origin country.";
		exit();
	}
	if ((int)$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0]->actions<=0) {
		echo "Invalid Training!\nYou are out of actions.";
		exit();
	}
	$count=-1;
	foreach ($save->continent as $cont) {
		foreach ($cont->country as $c) {
			if (strcmp((string)$c->owner, $_SESSION["username"])==0) {
				$count++;
			}
		}
	}
	if (strcmp((string)$save->xpath("/game/continent/country[name='" . (string)$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']/capital")[0] . "']")[0]->owner, $_SESSION["username"])!=0) {
		$count-=4;
	}
	$origin[0]->troops[0]->strength+=1000+100*$count;
	$player=$save->xpath("/game/players/player[username='" . $_SESSION["username"] . "']")[0];
	$player->actions-=1;
	$player->money-=50;
	save($save);
	exit();
}

function save($save)
{
	$save->asXML("save.xml");
	$_SESSION["saveFile"]=simplexml_load_file("save.xml")->asXML();
}

function endTurn()
{
	$save=simplexml_load_string($_SESSION["saveFile"]);
	if((int)$save->togo==count($save->players->player)) {
		$save->round+=1;
		$save->togo=1;
		$player=$save->xpath("/game/players/player[turnorder='1']");
		$player[0]->actions=3;
		#echo $player->username;
		$save->currentplayer=(string)$save->xpath("/game/players/player[turnorder='1']/username")[0];
	}
	else {
		$save->togo+=1;
		$player=$save->xpath("/game/players/player[turnorder='" . (int)$save->togo . "']");
		$player[0]->actions=3;
		$save->currentplayer=(string)$player[0]->username;
	}
	save($save);
	#echo "test";
	exit();
}
?>
<?php
session_start();
?>
<html>
<head>
	<title>Map Game</title>
	<meta charset="UTF-8">
	<script type="text/javascript" src="login.js"></script>
	<script src="http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/sha256.js"></script>
</head>
<body>
	<form id="login" action = "<?php echo $_SERVER["PHP_SELF"];?>" method="post">
		<?php
		ob_start();
		$users=simplexml_load_string($_SESSION["saveFile"])->players;
		$maxUsers=(int)$users->max;
		if (count($users->player)<$maxUsers) {
			echo "Requested Username: <input type=\"text\" name=\"username\"><br>
		Password: <input type=\"password\" name=\"password\"><br>
		Confirm Password: <input type=\"password\" name=\"confirmPassword\"><br>
		<input type=\"submit\" onclick=\"login()\" value=\"Submit\">";
		} else {
			echo "<script type='text/javascript'>alert('Game full! Cannot make account.');</script><br>";
			echo "<a href=\"index.php\">Click here to return.</a>";
		}
		?>
	</form>

	<?php
	if(isset($_POST["password"]))
	{
		$newSave=simplexml_load_string($_SESSION["saveFile"]);
		$users=$newSave->players->player;
		$_SESSION["lastLoginTime"]=time();
		$username = $_POST["username"];
		$PW= $_POST["password"];
		foreach ($users as $u) {
			if (strcmp((string)$u->username, $username)==0) {
				echo "<script type='text/javascript'>alert('Username taken! Cannot make account.');</script><br>";
				exit();
			}
		}
		$newSave->players->addChild("player")->addAttribute("id",count($users));
		#$newSave->players->player[count($users)-1]->addAttribute("id",count($users));
		$newSave->players->player[count($users)-1]->addChild("username",$username);
		$newSave->players->player[count($users)-1]->addChild("money",600);
		$newSave->players->player[count($users)-1]->addChild("turnorder",count($users));
		$newSave->players->player[count($users)-1]->addChild("actions",0);
		$newSave->players->player[count($users)-1]->addChild("color",mt_rand(0,25)*10 . "," . mt_rand(0,25)*10 . "," . mt_rand(0,25)*10);
		$newSave->players->player[count($users)-1]->addChild("password",$PW);
		$newSave->asXML("save.xml");
		$_SESSION["saveFile"]=$newSave->asXML();
		ob_end_clean();
		echo "<a href=\"index.php\">Account created! Click here to login.</a>";
	}
	?>
</body>
</html>
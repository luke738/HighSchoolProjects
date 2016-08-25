<?php
session_start();
if (!isset($_SESSION["validUser"])) {
	$_SESSION["validUser"]=-1;
}
if (!isset($_SESSION["lastLoginTime"])) {
	$_SESSION["lastLoginTime"]=time();
}
$_SESSION["saveFile"]=simplexml_load_file("save.xml")->asXML();
?>
<html>
<head>
	<title>Map Game</title>
	<meta charset="UTF-8">
	<script src="http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/sha256.js"></script>
	<script type="text/javascript" src="login.js"></script>
</head>
<body>
	<h1>Welcome to Map Game!</h1>
	<h2>Please login:</h2>

	<?php
	if ($_SESSION["validUser"]==0 && $_SESSION["lastLoginTime"]-time()>-10) {
		echo "User ID/Password not found.";
	}
	?>

	<form id="login" action="<?php echo $_SERVER["PHP_SELF"];?>" method="post">
		Username: <input type="text" name="ID"><br>
		Password: <input type="password" name="password"><br>
		<input type="submit" onclick="login()" value="Submit">
	</form>
	New? <a href="newUser.php">Create a new account.</a>

	<?php
	if(isset($_POST["ID"]))
	{
		$users=simplexml_load_string($_SESSION["saveFile"])->players->player;
		$_SESSION["lastLoginTime"]=time();
		$username = $_POST["ID"];
		$PW= $_POST["password"];
		foreach ($users as $u) {
			if (strcmp((string)$u->username . (string)$u->password, $username . $PW)==0) {
				$_SESSION["validUser"]=1;
				$_SESSION["username"]=$_POST["ID"];
				header("Location: game.php"); /* Redirect browser */
				exit();
			}
		}
		echo "<script type='text/javascript'>alert('Incorrect Username/Password!');</script>";
	}
	?>
</body>
</html>
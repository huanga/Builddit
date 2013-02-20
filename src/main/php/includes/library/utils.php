<?php

// quick function to convert hex back into binary value for encryption
function hex2bin($bindata)
{
   $len = strlen($bindata);
   return @pack("H" . $len, $bindata);
} 

// quick function to clean up the variables before we execute in SQL query
function quote_smart($value)
{
   // Stripslashes
   if (get_magic_quotes_gpc()) {
       $value = stripslashes($value);
   }
   // Quote if not integer
   if (!is_numeric($value)) {
       $value = mysql_real_escape_string($value);
   }
   return $value;
}

function load_language($filename, $forceload=false) {
	global $encoding, $lang;
	$cmd = "";
	$filename = "./includes/languages/" . $encoding . "." . $lang . "." . $filename . ".php";
	if ($forceload) {
		$cmd = "require (\"$filename\");";
	} else {
		$cmd = "require_once (\"$filename\");";
	}
	return ($cmd);
}

function load_template($filename, $forceload=false) {
	$cmd = "";
	$filename = "./includes/templates/" . $filename . ".php";
	if ($forceload) {
		$cmd = "require (\"$filename\");";
	} else {
		$cmd = "require_once (\"$filename\");";
	};
	return ($cmd);
}

function setsession($key, $value="") {
	$_SESSION[$key] = $value;
}

function unsetsession($key) {
	$_SESSION[$key] = null;
	unset($_SESSION[$key]);
}
?>
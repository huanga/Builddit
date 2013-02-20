<?php
	session_start();

	$sid = md5($_SERVER['REMOTE_ADDR']);
	session_id($sid);

	require_once ('./includes/config.php');
	require_once ('./includes/classes/database.php');

	// now, next order of business is to instanciate the database
	$database	=	new database($config["db_server"], $config["db_username"], $config["db_password"], $config["db_database"], $config["db_tbpfx"]);
	unset($config["db_server"], $config["db_username"], $config["db_password"], $config["db_database"], $config["db_tbpfx"]);
	
	// prepare our language settings
	$lang = $config["lang"];
	$encoding = $config["encoding"];
	unset($config["lang"], $config["encoding"]);
	
	// load our utilities and global language + templates
	require_once ('./includes/library/utils.php');
	eval(load_language ("global"));
	
	// instanciate our encryptor
	require_once ('./includes/classes/Blowfish.php');
	$blowfish			= new Crypt_Blowfish($config["bf_key"]);

	// well, we made it this far, we might as well record everything =]
	// first prepare the variables
	// we have: $user->_username, $sid - session, and THIS_SCRIPT
	$parms		= serialize(array_merge($_GET, $_POST, $_SESSION));
	$datetime	= date(DATE_RFC2822);
	header("Content-Transfer-Encoding: " . $encoding . "\n");

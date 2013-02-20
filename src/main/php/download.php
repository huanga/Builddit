<?php
// ####################### SET PHP ENVIRONMENT ###########################
error_reporting(E_ALL & ~E_NOTICE);

// #################### DEFINE IMPORTANT CONSTANTS #######################
define('THIS_SCRIPT', 		'download');

// ######################### REQUIRE BACK-END ############################
require_once('./includes/init.php');

// ############ REQUIRE (IF ANY) SCRIPT SPECIFIC FUNCTIONS ###############
require_once('./includes/classes/Blowfish.php');

// ######################### SCRIPT EXECUTION ############################
// fetch parameters
$encrypted	= $_GET['auth'];
$e_validate	= $_GET['key1'];
$r_validate	= $_GET['key2'];

// verify authenticy of authentication code
// phase 1, pre decryption
if (substr(md5($encrypted), 0, 8) != $e_validate)  {
    // hash have been tempered, reject
    header("HTTP/1.1 404 Not Found");
    exit();
}

// decrypt authentication code
$blowfish		= new Crypt_Blowfish($config["bf_key"]);
$unencrypted	= trim($blowfish->decrypt(hex2bin($encrypted)));

// verify authenticity of authentication code
// phase 2, post decryption
if (substr(md5($unencrypted), 0, 8) != $r_validate) {
    // hash have been tempered, reject
    header("HTTP/1.1 404 Not Found");
    exit();
}

// authentication code approved
list( $ipaddress, $timecode, $fileid ) = explode("||", $unencrypted);

// verify authenticy of client (IP mismatch = no go)
if ($_SERVER['REMOTE_ADDR'] != $ipaddress) {
    // hot linker detected, log details
    header("HTTP/1.1 404 Not Found");
    exit();
}

if ((time() - (60 * 60)) > $timecode) {
    // link was generated 60 minutes ago, not valid anymore!
    header("HTTP/1.1 404 Not Found");
    exit();
}

// Use $fileid to query against database for the actual file, and do stream_copy_to_stream() on it.
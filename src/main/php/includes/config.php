<?php
$config["db_server"]	=	"";	// MySQL server location
$config["db_username"]	=	"";	// MySQL username
$config["db_password"]	=	"";	// MySQL password
$config["db_database"]	=	"";	// MySQL database set
$config["db_tbpfx"]		=	"";	// MySQL table prefix, useful if you intend to put multiple application with this one

$config["bf_key"]		=	""; // This _MUST_ be a 32 digits long hex number

$config["encoding"]		=	"utf-8";
$config["lang"]			=	"en";

@include_once('./includes/config.local.php');   // Overide with local settings (put this file into .gitignore).
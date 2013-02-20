<?php
	/**
	** This code is written by Andy Huang, you are hereby granted none-exclusive
	** rights to use, and modify these code at your own will.  Redistribution without
	** his explicity written permission is strictly prohibited.
	**
	** Filename: /includes/classes/database.php
	** Date Started: 12/06/2005
	** Copyright: Copyright (c) 2005 Andy Huang
	** Author(s): Andy Huang
	**
	**/ 

class database {
	/** 
	** Class Variables
	** Variable Name		Type			Purpose
	** _server			   	String			The location of the server
	** _username			String			The username of the database
	** _password			String			The password of the database
	** _database			String			The database to use
	** _tablePrefix			String			The table prefix of our tables
	** _useCount			Integer			The amount of queries executed
	** _conn				Connection		The connection of our database
	**/

	var $_server			= 'localhost';
	var $_username			= 'username';
	var $_password			= 'password';
	var $_database			= 'database';
	var $_tablePrefix		= '';
	var $_conn 				= '';

	var $_useCount 			= 0;
	var $_timer 			= 0;

	/**
	** Class Functions
	** Constructor:
	**	database($Server, $Username, $Password, $Database, $TablePrefix)
	**
	** Destructor:
	**	__destruct()
	**
	** Core:
	**	close()
	**	execute($Query)
	**	open()
	**/

	function database($Server, $Username, $Password, $Database, $TablePrefix)
	{
		$this->_server			= $Server;
		$this->_username		= $Username;
		$this->_password 		= $Password;
		$this->_database		= $Database;
		$this->_tablePrefix		= $TablePrefix;

		$this->open();
	}

	function __destruct()
	{
		$this->close();
		unset($this->_server, $this->_username, $this->_password, $this->_database, $this->_tablePrefix, $this->_conn, $this->_useCount, $this->_timer);
	}

	function close()
	{
		// close quietly, even if connection failed
		@mysql_close($this->_conn);
	}

	function execute($Query)
	{
		$start = microtime();
		$result = mysql_query($Query, $this->_conn);
		$end = microtime();
		$exectime = round(($end - $start), 6);
		$this->_useCount = $this->_useCount + 1;
		$this->_timer = $this->_timer + $exectime;
		return $result;
	}

	function open()
	{
		$this->_conn = mysql_connect($this->_server, $this->_username, $this->_password);
		if (!$this->_conn)
		{
			die("Couldn't connect due to error: " . mysql_error());
		}
		mysql_select_db($this->_database);
	}
}
?>
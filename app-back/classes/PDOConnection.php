<?php

class PDOConnection extends PDO
{	
	public function __construct()
    {
		parent::__construct('mysql:dbname=vr_tourism;host=localhost', 'root', '');    
	}

}
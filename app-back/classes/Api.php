<?php

class Api
{
    // Récupére la connection pdo
    private $pdo;

    public function __construct($pdo) 
    {
        $this->pdo = $pdo;
        var_dump($this->pdo);
    }

    public function read()
    {
 
        // select all query
        $query = "SELECT * FROM api";
     

        $statement = $this->pdo->query($query);
     
        // execute query
        $statement->execute();
     
        return $statement;
    }
    
}
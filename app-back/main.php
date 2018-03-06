<?php
// required headers
//header("Access-Control-Allow-Origin: *");
//header("Content-Type: application/json; charset=UTF-8");
 
// include database and object files
include_once 'classes/PDOConnection.php';
include_once 'classes/Api.php';
 
// instantiate database and product object
$pdo = new PDOConnection();


// initialize object
$api = new Api($pdo);
 

// query products
$statement = $api->read();
$num = $statement->rowCount();
 
// check if more than 0 record found
if($num>0){
 
    // products array
    $api_arr=array();
    $api_arr["records"]=array();
 
    // retrieve our table contents
    // fetch() is faster than fetchAll()
    // http://stackoverflow.com/questions/2770630/pdofetchall-vs-pdofetch-in-a-loop
    while ($row = $statement->fetch(PDO::FETCH_ASSOC)){
        // extract row
        // this will make $row['name'] to
        // just $name only
        extract($row);
 
        $api_item=array(
            "id" => $id,
            "id_scanner" => $id_scanner,
            "lien_video" => $lien_video
        );
 
        array_push($api_arr["records"], $api_item);
    }
 
    echo json_encode($api_arr);
}
 
else{
    echo json_encode(
        array("message" => "No products found.")
    );
}
?>
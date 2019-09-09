<?php
function logg($message) {
    file_put_contents("log.txt", $message."\n", FILE_APPEND | LOCK_EX);
}
function error($message) {
    logg(print_r($message, true));
}

$pdo = new PDO("mysql:host=localhost;dbname=notesapp;charset=utf8",'notesapp','4mtcRR9YtZHw4AGd');
$pdo->exec("SET NAMES utf8");
$pdo->exec("SET CHARACTER SET utf8");

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $data = json_decode(file_get_contents("php://input"));
    logg("PUT ".json_encode($data));
    
    if (empty($data->id)) {
        
        file_put_contents("log.txt", "inserting...\n", FILE_APPEND | LOCK_EX);
        
        $sql = $pdo->prepare("INSERT INTO notes (name, text, changed) VALUES (?,?,?)");
        $sql->execute(array(
            $data->name,
            $data->text,
            $data->changed
        )) or error($sql->errorInfo());
        
        logg("inserted");
        
        echo json_encode(array(
            "id" => $pdo->lastInsertId()
        ));
        
    } else {
        
        logg("updating...");
        
        $sql = $pdo->prepare("UPDATE notes SET name = ?, text = ?, changed = ? WHERE id = ? AND changed <= ?");
        $sql->execute(array(
            $data->name,
            $data->text,
            $data->changed,
            $data->id,
            $data->changed
        )) or error($sql->errorInfo());
        
        logg("updated");
        
        echo json_encode(array(
            "id" => $data->id
        ));

    }
    
} else if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    
    logg("deleting...");
    $sql = $pdo->prepare("UPDATE notes SET deleted = 1, changed = NOW() WHERE id = ?");
    $sql->execute(array(
        $_GET["id"]
    )) or error($sql->errorInfo());
    logg("deleted");
    
} else {
    $sql = $pdo->prepare("SELECT * FROM notes WHERE changed >= ?");
    $sql->execute(array(
        $_GET["changedFrom"]
    )) or error($sql->errorInfo());
    $data = $sql->fetchAll(PDO::FETCH_ASSOC);
    foreach($data as &$row) {
        $row["deleted"] = (bool) $row["deleted"];
    }
    logg("GET ".$_GET["changedFrom"]." ".json_encode($data));
    
    header("Content-Type: application/json");
    echo json_encode($data);
}

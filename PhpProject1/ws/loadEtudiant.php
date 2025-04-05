<?php


if ($_SERVER["REQUEST_METHOD"] == "POST" || $_SERVER["REQUEST_METHOD"] == "GET") {
    include_once '../racine.php';
    include_once RACINE . '/service/EtudiantService.php';
    
    // Ensure that your service is returning something
    try {
        $es = new EtudiantService();
        $etudiants = $es->findAllApi();  // Assuming this returns an array or object
        
        // Check if there are results and return them
        if ($etudiants) {
            header('Content-type: application/json');
            echo json_encode(["success" => true, "data" => $etudiants]);
        } else {
            header('Content-type: application/json');
            echo json_encode(["success" => false, "message" => "No students found"]);
        }
    } catch (Exception $e) {
        header('Content-type: application/json');
        echo json_encode(["error" => $e->getMessage()]);
    }
}


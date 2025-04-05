<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// Hide PHP warnings/notices from output
ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
error_reporting(E_ALL);

include_once '../racine.php';
include_once RACINE.'/service/EtudiantService.php';

try {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['id'])) {
        throw new Exception("Données de requête invalides");
    }

    $es = new EtudiantService();
    $etudiant = $es->findById($data['id']);

    if (!$etudiant) {
        throw new Exception("Étudiant introuvable");
    }

    $deleted = $es->delete($etudiant);

    http_response_code(200);
    echo json_encode([
        'success' => $deleted,
        'message' => $deleted ? 'Étudiant supprimé' : 'Échec de la suppression'
    ]);
} catch(Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error' => $e->getMessage()
    ]);
}

<?php
// Set the correct content type for JSON
header('Content-Type: application/json');

// Log the start of the process
error_log("Received POST request");

// Check if the request method is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Log the raw POST data received
    $rawData = file_get_contents('php://input');
    error_log("Raw POST data: " . $rawData);

    // Decode the JSON body into an associative array
    $data = json_decode($rawData, true);

    // Log the decoded data
    error_log("Decoded data: " . print_r($data, true));

    // Check if the required parameters are present
    if ($data && isset($data['nom'], $data['prenom'], $data['ville'], $data['sexe'], $data['dateNaissance'], $data['image'])) {
        // Extract values from the decoded JSON
        $nom = $data['nom'];
        $prenom = $data['prenom'];
        $ville = $data['ville'];
        $sexe = $data['sexe'];
        $dateNaissance = $data['dateNaissance'];
        $image = $data['image'];  // This could be a URL or base64 encoded image data

        // Convert date to proper format (YYYY-MM-DD)
        $dateNaissance = date('Y-m-d', strtotime($dateNaissance));

        // Log the extracted data
        error_log("Extracted data: nom=$nom, prenom=$prenom, ville=$ville, sexe=$sexe, dateNaissance=$dateNaissance, image=$image");

        // Include necessary files and create the Etudiant
        include_once '../racine.php';
        include_once RACINE.'/service/EtudiantService.php';

        // Log before creating the Etudiant
        error_log("Creating Etudiant with the extracted data");

        // You can store the image as base64 or save it as a file on the server, depending on your preference
        // If you choose base64, save it directly in the database, else save the file path.

        $es = new EtudiantService();
        $es->create(new Etudiant(1, $nom, $prenom, $ville, $sexe, $dateNaissance, $image));

        // Send a success response
        echo json_encode(['success' => true, 'message' => 'Etudiant created successfully']);
    } else {
        // If any parameters are missing, return an error response
        error_log("Missing parameters: " . print_r($data, true));
        echo json_encode(['error' => 'Missing parameters']);
    }
} else {
    // Handle invalid request method
    error_log("Invalid request method");
    echo json_encode(['error' => 'Invalid request method']);
}
?>

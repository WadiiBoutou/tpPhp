<?php
include_once '../racine.php';
include_once RACINE.'/service/EtudiantService.php';


extract($_POST); 

$es = new EtudiantService();
$es->create(new Etudiant(1, $nom, $prenom, $ville, $sexe, $dateNaissance, $image));

header("location:../index.php");

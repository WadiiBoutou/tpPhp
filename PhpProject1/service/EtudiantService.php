<?php
include_once RACINE . '/classes/Etudiant.php';
include_once RACINE . '/connexion/Connexion.php';
include_once RACINE . '/dao/IDao.php';
class EtudiantService implements IDao {
    private $connexion;
    function __construct() {
        $this->connexion = new Connexion();
    }

    public function create($o) {
        $query = "INSERT INTO Etudiant (nom, prenom, ville, sexe, dateNaissance, image) VALUES (:nom, :prenom, :ville, :sexe, :dateNaissance, :image)";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->bindValue(':nom', $o->getNom());
        $req->bindValue(':prenom', $o->getPrenom());
        $req->bindValue(':ville', $o->getVille());
        $req->bindValue(':sexe', $o->getSexe());
        $req->bindValue(':dateNaissance', $o->getDateNaissance());
        $req->bindValue(':image', $o->getImage());
        $req->execute() or die('Erreur SQL');
    }

public function delete($o) {
    $query = "DELETE FROM Etudiant WHERE id = :id";
    $req = $this->connexion->getConnexion()->prepare($query);
    $req->bindValue(':id', $o->getId(), PDO::PARAM_INT);
    
    return $req->execute(); 
}

public function findAll() {
    $etds = array();
    $query = "SELECT * FROM Etudiant";
    $req = $this->connexion->getConnexion()->prepare($query);
    $req->execute();
    while ($e = $req->fetch(PDO::FETCH_OBJ)) {
        $etds[] = new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe, $e->dateNaissance ,$e->image);
    }
    return $etds;
}

public function findById($id) {
    $query = "SELECT * FROM Etudiant WHERE id = :id";
    $req = $this->connexion->getConnexion()->prepare($query);
    $req->bindValue(':id', $id, PDO::PARAM_INT);
    $req->execute();
    if ($e = $req->fetch(PDO::FETCH_OBJ)) {
        return new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe, $e->dateNaissance ,$e->image);
    }
    return null;
}

public function update($o) {
    $query = "UPDATE Etudiant SET nom = :nom, prenom = :prenom, ville = :ville, sexe = :sexe WHERE id = :id";
    $req = $this->connexion->getConnexion()->prepare($query);
    $req->bindValue(':nom', $o->getNom());
    $req->bindValue(':prenom', $o->getPrenom());
    $req->bindValue(':ville', $o->getVille());
    $req->bindValue(':sexe', $o->getSexe());
    $req->bindValue(':id', $o->getId(), PDO::PARAM_INT);
    $req->execute() or die('Erreur SQL');
}
public function findAllApi() {
    $query = "SELECT * FROM Etudiant";
    $req = $this->connexion->getConnexion()->prepare($query);
    $req->execute();
    $result = $req->fetchAll(PDO::FETCH_ASSOC);

    // Debug: Check if any rows are returned
    if (empty($result)) {
        echo "No data found";
    }
    return $result;
}


}
package com.radisson.collection.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.radisson.collection.entities.Visiteur;

public interface VisiteurRepository extends JpaRepository<Visiteur, Long>{
	public List<Visiteur> findByStatus(String status) ;
	public List<Visiteur> findByNomOrPrenomContains(String nom, String prenom) ;
	public Visiteur findByPieceNumero(String numero) ;
	public List<Visiteur> findByDate(String date) ;
	public List<Visiteur> findByEntreprise(String entreprise) ;
	public List<Visiteur> findByImmatriculation(String immatriculation) ;
	public List<Visiteur> findByDateBetween(String date1, String date2) ;
}

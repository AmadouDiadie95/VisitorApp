package com.radisson.collection.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @ToString
public class Visiteur {
	@Id @GeneratedValue(strategy = GenerationType.AUTO) 
	private Long id ;
	private String nom ; 
	private String prenom ;
	private String piece_identite ;
	private String pieceNumero ;
	private String departement ;
	private String personne_visite ;
	private double badge ;
	private String telephone ;
	private String date ;
	private String entree ;
	private String sortie ;
	private String status ;
	private String fournisseur ;
	private String entreprise ;
	private String type_vehicule ;
	private String immatriculation ;
	private String agent ;
	private String nom_agent ;
	@Lob
	private String image;
	@Lob @Transient
	private MultipartFile file ;
	private Date heureEnregistrement ;
}

package com.radisson.collection.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @ToString
public class CurrentLicense {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;
	private String licenseKey ;
	private Date licenseActivationDate ;
	private Date licenseExpirationDate ;
	private Boolean isExpired ;
}

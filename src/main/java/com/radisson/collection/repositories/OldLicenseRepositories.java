package com.radisson.collection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.radisson.collection.entities.OldLicense;

public interface OldLicenseRepositories extends JpaRepository<OldLicense, Long> {
	public OldLicense findByLicenseKey(String key) ;
	public OldLicense findByLicenseActivationDate(String date) ; 
	public OldLicense findByLicenseExpirationDate(String date) ; 
	// public List<OldLicense> findByIsExpired(Boolean isExperied) ;
}

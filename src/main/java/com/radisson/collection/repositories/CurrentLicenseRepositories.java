package com.radisson.collection.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.radisson.collection.entities.CurrentLicense;

public interface CurrentLicenseRepositories extends JpaRepository<CurrentLicense, Long> {
	public CurrentLicense findByLicenseKey(String key) ;
	public CurrentLicense findByLicenseActivationDate(String date) ; 
	public CurrentLicense findByLicenseExpirationDate(String date) ; 
	public List<CurrentLicense> findByIsExpired(Boolean isExperied) ;
}

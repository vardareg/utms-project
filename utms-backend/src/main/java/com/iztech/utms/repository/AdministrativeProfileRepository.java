package com.iztech.utms.repository;

import com.iztech.utms.model.AdministrativeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrativeProfileRepository extends JpaRepository<AdministrativeProfile, Long> {
}

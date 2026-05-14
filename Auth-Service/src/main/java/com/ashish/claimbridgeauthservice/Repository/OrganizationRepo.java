package com.ashish.claimbridgeauthservice.Repository;

import com.ashish.claimbridgeauthservice.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepo  extends JpaRepository<Organization, String> {

}

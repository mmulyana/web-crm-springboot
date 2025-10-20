package com.crm.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.web.models.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
  public AppUser findByEmail(String email);

  public AppUser findByUsername(String username);
}

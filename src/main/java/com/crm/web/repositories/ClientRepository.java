package com.crm.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.web.models.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
  public Client findByEmail(String email);
}

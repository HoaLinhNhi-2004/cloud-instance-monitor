package com.techvalley.monitor.repository;

import com.techvalley.monitor.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByManagerId(Long managerId);
}

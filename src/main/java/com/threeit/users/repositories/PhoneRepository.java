package com.threeit.users.repositories;

import com.threeit.users.entities.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhoneRepository extends JpaRepository<Phone, UUID> {
}

package com.nisum.exam.userapi.repository;

import com.nisum.exam.userapi.entity.PhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para gestionar la entidad PhoneEntity en la base de datos.
 */
public interface PhoneRepository extends JpaRepository<PhoneEntity, Long> {
}

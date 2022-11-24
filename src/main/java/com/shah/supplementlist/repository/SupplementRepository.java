package com.shah.supplementlist.repository;

import com.shah.supplementlist.model.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplementRepository extends JpaRepository<Supplement, UUID> {
}

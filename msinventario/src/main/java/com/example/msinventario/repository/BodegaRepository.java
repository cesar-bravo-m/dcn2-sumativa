package com.example.msinventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.msinventario.model.Bodega;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Integer> {
}

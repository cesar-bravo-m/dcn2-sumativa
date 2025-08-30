package com.example.msinventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.msinventario.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
}

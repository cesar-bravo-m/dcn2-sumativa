package com.example.msinventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.msinventario.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}

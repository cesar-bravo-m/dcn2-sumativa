package com.function.consumidores.categoria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    
    private static final String DB_URL = "jdbc:postgresql://20.81.136.128:5432/duoc";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "84oL4mK6cM8w7SK";
    
    public List<CategoriaDTO> getAllCategorias() throws SQLException {

        List<CategoriaDTO> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categoria";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
        } catch (Exception e) {
            System.out.println("### Error:"+e);
        }
        
        return categorias;
    }
    
    public CategoriaDTO getCategoriaById(Long categoriaId) throws SQLException {
        String sql = "SELECT id, nombre FROM categoria WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCategoria(rs);
            }
        }
        
        return null;
    }
    
    public CategoriaDTO createCategoria(CategoriaDTO categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nombre) " +
                    "VALUES (?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            System.out.println("=== 1");
            stmt.setString(1, categoria.getNombre());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                categoria.setId(rs.getLong("id"));
                return categoria;
            }
        }
        
        return null;
    }
    
    public boolean updateCategoria(CategoriaDTO categoria) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setLong(2, categoria.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteCategoria(Long categoriaId) throws SQLException {

        String sql = "DELETE FROM categoria WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private void deleteInventario(Long categoriaId) throws SQLException {
        String sql = "DELETE FROM inventario WHERE categoria_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            stmt.executeUpdate();
        }
    }

    private CategoriaDTO mapResultSetToCategoria(ResultSet rs) throws SQLException {
        CategoriaDTO categoria = new CategoriaDTO();
        categoria.setId(rs.getLong("id"));
        categoria.setNombre(rs.getString("nombre"));
        
        return categoria;
    }
    
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}

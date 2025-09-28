package com.function.consumidores.categoria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.function.shared.DatabaseConfig;

public class DatabaseService {
    
    
    public CategoriaDTO getCategoriaById(Long categoriaId) throws SQLException {
        String sql = "SELECT id, nombre FROM categoria WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                CategoriaDTO categoria = new CategoriaDTO();
                categoria.setId(rs.getLong("id"));
                categoria.setNombre(rs.getString("nombre"));
                return categoria;
            }
        }
        
        return null;
    }
    
    public CategoriaDTO createCategoria(CategoriaDTO categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nombre) " +
                    "VALUES (?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setLong(2, categoria.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteCategoria(Long categoriaId) throws SQLException {

        String sql = "DELETE FROM categoria WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private void deleteInventario(Long categoriaId) throws SQLException {
        String sql = "DELETE FROM inventario WHERE categoria_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            stmt.executeUpdate();
        }
    }

    
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
            return conn.isValid(DatabaseConfig.DB_CONNECTION_TIMEOUT);
        } catch (SQLException e) {
            return false;
        }
    }
}

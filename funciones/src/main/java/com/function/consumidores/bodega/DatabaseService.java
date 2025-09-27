package com.function.consumidores.bodega;

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
    
    public List<BodegaDTO> getAllBodegas() throws SQLException {
        List<BodegaDTO> bodegas = new ArrayList<>();
        
        String sql = "SELECT id, nombre, ubicacion FROM bodega";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bodegas.add(mapResultSetToBodega(rs));
            }
        }
        
        return bodegas;
    }
    
    public BodegaDTO getBodegaById(Long bodegaId) throws SQLException {
        String sql = "SELECT id, nombre, ubicacion FROM bodega WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, bodegaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBodega(rs);
            }
        }
        
        return null;
    }
    
    public BodegaDTO createBodega(BodegaDTO bodega) throws SQLException {
        String sql = "INSERT INTO bodega (nombre, ubicacion) " +
                    "VALUES (?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bodega.getNombre());
            stmt.setString(2, bodega.getUbicacion());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                bodega.setId(rs.getLong("id"));
                return bodega;
            }
        }
        
        return null;
    }
    
    public boolean updateBodega(BodegaDTO bodega) throws SQLException {
        String sql = "UPDATE bodega SET nombre = ?, ubicacion = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bodega.getNombre());
            stmt.setString(2, bodega.getUbicacion());
            stmt.setLong(3, bodega.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteBodega(Long bodegaId) throws SQLException {

        // eliminando inventario asociado
        this.deleteInventario(bodegaId);

        String sql = "DELETE FROM bodega WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, bodegaId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private void deleteInventario(Long bodegaId) throws SQLException {
        String sql = "DELETE FROM inventario WHERE bodega_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, bodegaId);
            stmt.executeUpdate();
        }
    }

    private BodegaDTO mapResultSetToBodega(ResultSet rs) throws SQLException {
        BodegaDTO bodega = new BodegaDTO();
        bodega.setId(rs.getLong("id"));
        bodega.setNombre(rs.getString("nombre"));
        bodega.setUbicacion(rs.getString("ubicacion"));
        
        return bodega;
    }
    
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}

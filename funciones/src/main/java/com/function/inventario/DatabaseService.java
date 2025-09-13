package com.function.inventario;

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
    
    public List<InventarioDTO> getAllInventarios() throws SQLException {
        List<InventarioDTO> inventarios = new ArrayList<>();
        
        String sql = "SELECT id, producto_id, bodega_id, cantidad FROM inventario ORDER BY id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                inventarios.add(mapResultSetToInventario(rs));
            }
        }
        
        return inventarios;
    }
    
    public InventarioDTO getInventarioById(Integer inventarioId) throws SQLException {
        String sql = "SELECT id, producto_id, bodega_id, cantidad FROM inventario WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, inventarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToInventario(rs);
            }
        }
        
        return null;
    }
    
    public List<InventarioDTO> getInventariosByProductoId(Integer productoId) throws SQLException {
        List<InventarioDTO> inventarios = new ArrayList<>();
        
        String sql = "SELECT id, producto_id, bodega_id, cantidad FROM inventario WHERE producto_id = ? ORDER BY id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                inventarios.add(mapResultSetToInventario(rs));
            }
        }
        
        return inventarios;
    }
    
    public List<InventarioDTO> getInventariosByBodegaId(Integer bodegaId) throws SQLException {
        List<InventarioDTO> inventarios = new ArrayList<>();
        
        String sql = "SELECT id, producto_id, bodega_id, cantidad FROM inventario WHERE bodega_id = ? ORDER BY id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bodegaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                inventarios.add(mapResultSetToInventario(rs));
            }
        }
        
        return inventarios;
    }
    
    public InventarioDTO createInventario(InventarioDTO inventario) throws SQLException {
        String sql = "INSERT INTO inventario (producto_id, bodega_id, cantidad) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, inventario.getProductoId());
            stmt.setInt(2, inventario.getBodegaId());
            stmt.setInt(3, inventario.getCantidad());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                inventario.setId(rs.getInt("id"));
                return inventario;
            }
        }
        
        return null;
    }
    
    public boolean updateInventario(InventarioDTO inventario) throws SQLException {
        String sql = "UPDATE inventario SET producto_id = ?, bodega_id = ?, cantidad = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, inventario.getProductoId());
            stmt.setInt(2, inventario.getBodegaId());
            stmt.setInt(3, inventario.getCantidad());
            stmt.setInt(4, inventario.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteInventario(Integer inventarioId) throws SQLException {
        String sql = "DELETE FROM inventario WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, inventarioId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private InventarioDTO mapResultSetToInventario(ResultSet rs) throws SQLException {
        InventarioDTO inventario = new InventarioDTO();
        inventario.setId(rs.getInt("id"));
        inventario.setProductoId(rs.getInt("producto_id"));
        inventario.setBodegaId(rs.getInt("bodega_id"));
        inventario.setCantidad(rs.getInt("cantidad"));
        
        return inventario;
    }
    
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}
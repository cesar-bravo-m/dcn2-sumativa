package com.function.consumidores.producto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.function.consumidores.inventario.InventarioDTO;
import com.function.shared.DatabaseConfig;

public class DatabaseService {
    
    

    
    public ProductoDTO createProducto(ProductoDTO producto) throws SQLException {
        String sql = "INSERT INTO producto (nombre, categoria) VALUES (?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setLong(2, producto.getCategoria());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                producto.setId(rs.getInt("id"));
                return producto;
            }
        }
        
        return null;
    }
    
    public boolean updateProducto(ProductoDTO producto) throws SQLException {
        String sql = "UPDATE producto SET nombre = ?, categoria = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setLong(2, producto.getCategoria());
            stmt.setInt(3, producto.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteProducto(Integer productoId) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productoId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public ProductoDTO getProductoById(Integer productoId) throws SQLException {
        String sql = "SELECT id, nombre, categoria FROM producto WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ProductoDTO producto = new ProductoDTO();
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCategoria(rs.getLong("categoria"));
                return producto;
            }
        }
        
        return null;
    }
    
    public Integer getFirstAvailableBodegaId() throws SQLException {
        String sql = "SELECT id FROM bodega ORDER BY id ASC LIMIT 1";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        
        return null;
    }
    
    public InventarioDTO createInventario(InventarioDTO inventario) throws SQLException {
        String sql = "INSERT INTO inventario (producto_id, bodega_id, cantidad) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
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
    
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
            return conn.isValid(DatabaseConfig.DB_CONNECTION_TIMEOUT);
        } catch (SQLException e) {
            return false;
        }
    }
}

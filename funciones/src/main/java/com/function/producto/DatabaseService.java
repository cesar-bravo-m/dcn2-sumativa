package com.function.producto;

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
    
    public List<ProductoDTO> getAllProductos() throws SQLException {
        List<ProductoDTO> productos = new ArrayList<>();
        
        String sql = "SELECT id, nombre, categoria FROM producto ORDER BY id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        
        return productos;
    }
    
    public ProductoDTO getProductoById(Integer productoId) throws SQLException {
        String sql = "SELECT id, nombre, categoria FROM producto WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProducto(rs);
            }
        }
        
        return null;
    }
    

    
    public ProductoDTO createProducto(ProductoDTO producto) throws SQLException {
        String sql = "INSERT INTO producto (nombre, categoria) VALUES (?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            
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
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productoId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private ProductoDTO mapResultSetToProducto(ResultSet rs) throws SQLException {
        ProductoDTO producto = new ProductoDTO();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setCategoria(rs.getLong("categoria"));
        
        return producto;
    }
    
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}

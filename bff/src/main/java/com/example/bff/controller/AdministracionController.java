package com.example.bff.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;



@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdministracionController {

        private static final Logger logger = LoggerFactory.getLogger(AdministracionController.class);
        private final RestTemplate restTemplate = new RestTemplate();
        private final ObjectMapper objectMapper = new ObjectMapper();
        
        @Value("${azure.functions.admin.url}")
        private String azureFunctionUrl;

        @GetMapping("/bodega")
        public ResponseEntity<String> getBodegas() {
                try {
                        logger.info("Obteniendo lista de bodegas");
                        ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/bodega", String.class);
                        String sortedResponse = sortJsonArrayById(response.getBody());
                        logger.info("Lista de bodegas obtenida exitosamente");
                        return ResponseEntity.ok(sortedResponse);
                } catch (RestClientException e) {
                        logger.error("Error al obtener bodegas desde Azure Function: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al obtener bodegas\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al obtener bodegas: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

	@GetMapping("/bodega/{id}")
	public ResponseEntity<String> getBodegaById(@PathVariable Long id) {
		try {
			logger.info("Obteniendo bodega con ID: {}", id);
			ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/bodega?id="+id, String.class);
			logger.info("Bodega con ID {} obtenida exitosamente", id);
			return ResponseEntity.ok(response.getBody());
		} catch (RestClientException e) {
			logger.error("Error al obtener bodega con ID {}: {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("{\"error\":\"Error interno del servidor al obtener la bodega\"}");
		} catch (Exception e) {
			logger.error("Error inesperado al obtener bodega con ID {}: {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
		}
	}

	@PostMapping("/bodega")
	public ResponseEntity<String> setBodega(@RequestBody String entity) {
		try {
			logger.info("Creando nueva bodega");
			ResponseEntity<String> response = restTemplate.postForEntity(azureFunctionUrl+"/bodega", entity, String.class);
			logger.info("Bodega creada exitosamente");
			return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
		} catch (RestClientException e) {
			logger.error("Error al crear bodega: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("{\"error\":\"Error interno del servidor al crear la bodega\"}");
		} catch (Exception e) {
			logger.error("Error inesperado al crear bodega: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
		}
	}

        @PutMapping("/bodega/{id}")
        public ResponseEntity<String> updateBodega(@PathVariable Long id, @RequestBody String entity) {
                try {
                        logger.info("Actualizando bodega con ID: {}", id);
                        HttpEntity<String> httpEntity = new HttpEntity<>(entity);
                        ResponseEntity<String> response = restTemplate.exchange(azureFunctionUrl+"/bodega?id="+id, HttpMethod.PUT, httpEntity, String.class);
                        logger.info("Bodega con ID {} actualizada exitosamente", id);
                        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al actualizar bodega con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al actualizar la bodega\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al actualizar bodega con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        @DeleteMapping("/bodega/{id}")
        public ResponseEntity<String> deleteBodega(@PathVariable Long id) {
                try {
                        logger.info("Eliminando bodega con ID: {}", id);
                        ResponseEntity<String> response = restTemplate.exchange(azureFunctionUrl+"/bodega?id="+id, HttpMethod.DELETE, null, String.class);
                        logger.info("Bodega con ID {} eliminada exitosamente", id);
                        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al eliminar bodega con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al eliminar la bodega\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al eliminar bodega con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }


        @GetMapping("/categoria")
        public ResponseEntity<String> getCategorias() {
                try {
                        logger.info("Obteniendo lista de categorías");
                        ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/categoria", String.class);
                        String sortedResponse = sortJsonArrayById(response.getBody());
                        logger.info("Lista de categorías obtenida exitosamente");
                        return ResponseEntity.ok(sortedResponse);
                } catch (RestClientException e) {
                        logger.error("Error al obtener categorías desde Azure Function: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al obtener categorías\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al obtener categorías: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        @GetMapping("/categoria/{id}")
        public ResponseEntity<String> getCategoriaById(@PathVariable Long id) {
                try {
                        logger.info("Obteniendo categoría con ID: {}", id);
                        ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/categoria?id="+id, String.class);
                        logger.info("Categoría con ID {} obtenida exitosamente", id);
                        return ResponseEntity.ok(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al obtener categoría con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al obtener la categoría\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al obtener categoría con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        @PostMapping("/categoria")
        public ResponseEntity<String> setCategoria(@RequestBody String entity) {
                try {
                        logger.info("Creando nueva categoría");
                        ResponseEntity<String> response = restTemplate.postForEntity(azureFunctionUrl+"/categoria", entity, String.class);
                        logger.info("Categoría creada exitosamente");
                        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al crear categoría: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al crear la categoría\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al crear categoría: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        @PutMapping("/categoria/{id}")
        public ResponseEntity<String> updateCategoria(@PathVariable Long id, @RequestBody String entity) {
                try {
                        logger.info("Actualizando categoría con ID: {}", id);
                        HttpEntity<String> httpEntity = new HttpEntity<>(entity);
                        ResponseEntity<String> response = restTemplate.exchange(azureFunctionUrl+"/categoria?id="+id, HttpMethod.PUT, httpEntity, String.class);
                        logger.info("Categoría con ID {} actualizada exitosamente", id);
                        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al actualizar categoría con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al actualizar la categoría\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al actualizar categoría con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        @GetMapping("/producto")
        public ResponseEntity<String> getProductos() {
                try {
                        logger.info("Obteniendo lista de productos");
                        ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/producto", String.class);
                        String sortedResponse = sortJsonArrayById(response.getBody());
                        logger.info("Lista de productos obtenida exitosamente");
                        return ResponseEntity.ok(sortedResponse);
                } catch (RestClientException e) {
                        logger.error("Error al obtener productos desde Azure Function: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al obtener productos\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al obtener productos: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        @GetMapping("/producto/{id}")
        public ResponseEntity<String> getProductoById(@PathVariable Long id) {
                try {
                        logger.info("Obteniendo producto con ID: {}", id);
                        ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/producto?id="+id, String.class);
                        logger.info("Producto con ID {} obtenido exitosamente", id);
                        return ResponseEntity.ok(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al obtener producto con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al obtener el producto\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al obtener producto con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        @PostMapping("/producto")
        public ResponseEntity<String> setProducto(@RequestBody String entity) {
                try {
                        logger.info("Creando nuevo producto");
                        ResponseEntity<String> response = restTemplate.postForEntity(azureFunctionUrl+"/producto", entity, String.class);
                        logger.info("Producto creado exitosamente");
                        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al crear producto: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al crear el producto\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al crear producto: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }
        
        @PutMapping("/producto/{id}")
        public ResponseEntity<String> updateProducto(@PathVariable Long id, @RequestBody String entity) {
                try {
                        logger.info("Actualizando producto con ID: {}", id);
                        HttpEntity<String> httpEntity = new HttpEntity<>(entity);
                        ResponseEntity<String> response = restTemplate.exchange(azureFunctionUrl+"/producto?id="+id, HttpMethod.PUT, httpEntity, String.class);
                        logger.info("Producto con ID {} actualizado exitosamente", id);
                        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al actualizar producto con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al actualizar el producto\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al actualizar producto con ID {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }

        /**
         * Ordena un array JSON por el campo 'id' de forma ascendente
         */
        private String sortJsonArrayById(String jsonResponse) {
                try {
                        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                                return jsonResponse;
                        }
                        
                        JsonNode rootNode = objectMapper.readTree(jsonResponse);
                        
                        // Si es un array, ordenarlo por ID
                        if (rootNode.isArray()) {
                                ArrayNode arrayNode = (ArrayNode) rootNode;
                                List<JsonNode> nodeList = new ArrayList<>();
                                
                                // Convertir a lista para poder ordenar
                                arrayNode.forEach(nodeList::add);
                                
                                // Ordenar por ID
                                nodeList.sort(Comparator.comparing(node -> {
                                        JsonNode idNode = node.get("id");
                                        return idNode != null ? idNode.asLong(0) : 0L;
                                }));
                                
                                // Crear nuevo array ordenado
                                ArrayNode sortedArray = objectMapper.createArrayNode();
                                nodeList.forEach(sortedArray::add);
                                
                                return objectMapper.writeValueAsString(sortedArray);
                        }
                        
                        // Si no es un array, devolver tal como está
                        return jsonResponse;
                        
                } catch (Exception e) {
                        logger.warn("Error al ordenar respuesta JSON por ID, devolviendo respuesta original: {}", e.getMessage());
                        return jsonResponse;
                }
        }

}
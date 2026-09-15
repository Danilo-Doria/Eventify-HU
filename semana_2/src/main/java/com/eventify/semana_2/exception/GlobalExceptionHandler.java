package com.eventify.semana_2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler Especifica qué tipo de excepción va a capturar este metodo.
    // En este caso, atrapará cualquier RuntimeException (o subclases de esta) lanzada en los controladores.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        // Se crea un mapa clave-valor para armar la estructura personalizada del JSON de respuesta.
        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());

        // Agrega el código de estado numérico (404)
        body.put("status", HttpStatus.NOT_FOUND.value());

        // Agrega la descripción corta del código HTTP
        body.put("error", "Not Found");

        // Agrega el mensaje de error personalizado que venía dentro de la excepción (ex.getMessage())
        body.put("message", ex.getMessage());

        // Devuelve el ResponseEntity especificando el estado 404 NOT FOUND
        // y adjunta el mapa 'body' para que Spring lo convierta en JSON.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }
}
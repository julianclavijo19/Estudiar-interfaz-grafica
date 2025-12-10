package com.ejemplo.model;

/**
 * Enum que representa las posiciones posibles de un jugador en el campo.
 * Ideal para usar directamente en ComboBox o ChoiceBox de JavaFX.
 */
public enum Posicion {
    PORTERO,
    DEFENSA,
    MEDIOCAMPISTA,
    DELANTERO;

    /**
     * Devuelve el nombre con la primera letra en mayúscula y el resto en minúscula,
     * para mostrar de forma amigable en la interfaz.
     */
    @Override
    public String toString() {
        String nombre = name().toLowerCase();
        return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
    }
}
package com.example.rendimientoplanta.vo

import java.lang.Exception

/**
 * Una sealed class es una clase sellada, recibe un "out T" que viene siendo cualquier
 * tipo de dato
 * Sus metodos reciben cualquier tipo de dato
 * Es utilizada en las actividades o fragmentos para saber el estado de la respuesta del backend
 */
sealed class Resource<out T> {
    /**
     * Esto sucede cuando carga
     */
    class Loading<out T> : Resource<T>()

    /**
     * Esto sucede cuando se trae los datos exitosamente
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Y esto cuando ocurre un error
     */
    data class Failure(val exception: Exception) : Resource<Nothing>()
}
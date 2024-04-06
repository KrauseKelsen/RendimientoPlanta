package com.example.rendimientoplanta.base.builder

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

object StringBuilder {
    fun getStrings(anys: ArrayList<Any>, field: String): ArrayList<String> {
        val array = ArrayList<String>()
        for (any in anys){
            val hashmap = ObjectMapper().convertValue(any, object : TypeReference<Map<String, Any>>() {})
            array.add(hashmap[field].toString())
        }
        return array
    }
}
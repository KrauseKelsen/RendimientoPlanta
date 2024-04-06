package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Token
import com.google.firebase.firestore.DocumentChange

object TokenBuilder {
    fun tokenBuilder(change: DocumentChange) = Token(change.document.id, change.document["dispositivo"].toString(), change.document["estado"].toString().toBoolean())
}
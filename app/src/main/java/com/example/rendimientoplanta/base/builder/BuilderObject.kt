package com.example.rendimientoplanta.base.builder

import com.google.firebase.firestore.DocumentSnapshot

interface BuilderObject {
    fun documentSnapshotToObject(document: DocumentSnapshot) = Any()
}
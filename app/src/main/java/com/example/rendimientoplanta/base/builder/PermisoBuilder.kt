package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Permiso
import com.google.firebase.firestore.DocumentSnapshot

class PermisoBuilder : BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) = Permiso(document.id, document.get("nombres") as ArrayList<String>, document.get("sql").toString().toBoolean())
}
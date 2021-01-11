package com.kit.foodapp

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

@IgnoreExtraProperties
data class FoodDeliveryData(
        val title: String? = "",
        val imagePath: String? = "",
        val description: String? = "",
        val userName: String? = "",
        val userAddress: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "title" to title,
                "imagePath" to imagePath,
                "description" to description,
                "userName" to userName,
                "userAddress" to userAddress
        )
    }
}

class FoodDeliveryDatabaseController(refName: String = "foods") {

    private val database: FirebaseDatabase = Firebase.database
    private val foodsRef: DatabaseReference

    init {
        foodsRef = database.getReference(refName)
    }

    fun upload(title: String, imagePath: String, description: String, userName: String, userAddress: String) {
        val documentId = List(25) { ( ('a'..'z') + ('A'..'Z') + ('0'..'9') ).random() }.joinToString("")
        val foodData = FoodDeliveryData(title, imagePath, description, userName, userAddress)
        val childUpdates = hashMapOf<String, Any>( "/$documentId" to foodData.toMap() )
        foodsRef.updateChildren(childUpdates)
    }

    fun download(onChange: (List<FoodDeliveryData>) -> Unit) {
        foodsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childrens = dataSnapshot.children
                val foodDeliveryDatas = mutableListOf<FoodDeliveryData>()
                for (child in childrens) {
                    foodDeliveryDatas.add( FoodDeliveryData(
                            child.child("title").value as String?,
                            child.child("imagePath").value as String?,
                            child.child("description").value as String?,
                            child.child("userName").value as String?,
                            child.child("userAddress").value as String?
                    ) )
                }
                onChange(foodDeliveryDatas)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelled", "error:", error.toException())
            }
        })
    }

}


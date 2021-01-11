package com.kit.foodapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DetailsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val title = intent.getStringExtra("title")
        val imagePath = intent.getStringExtra("imagePath")
        val description = intent.getStringExtra("description")
        val userName = intent.getStringExtra("userName")
        val userAddress = intent.getStringExtra("userAddress")
        val food = FoodDeliveryData(title, imagePath, description, userName, userAddress)

        Log.d("DEMO", food.toString())

        val titleView: TextView = findViewById(R.id.title_view)
        val imageView: ImageView = findViewById(R.id.image_view)
        val descriptionView: TextView = findViewById(R.id.description_view)
        val userNameView: TextView = findViewById(R.id.username_view)
        val userAddressView: TextView = findViewById(R.id.useraddress_view)

        titleView.text = "料理名: " + title.toString()
        descriptionView.text = "詳細説明:\n" + description.toString()
        userNameView.text = "投稿者: " + userName.toString()
        userAddressView.text = "届先: " + userAddress.toString()
        val strage: FirebaseStorage = Firebase.storage
        val imageRef = strage.getReference(food.imagePath.toString())
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                    .load(uri.toString())
                    .into(imageView)
        }

    }

}
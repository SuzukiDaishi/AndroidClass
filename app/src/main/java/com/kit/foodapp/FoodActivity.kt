package com.kit.foodapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class FoodActivity: AppCompatActivity() {

    private val storage: FirebaseStorage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        val imageView: ImageView = findViewById(R.id.edit_foodImage)
        imageView.setOnClickListener {
            selectPhoto()
        }

        val button: Button = findViewById(R.id.send_button)
        button.setOnClickListener {
            sendFoodData()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != RESULT_OK) { return }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        val imageView = findViewById<ImageView>(R.id.edit_foodImage)
                        imageView.setImageBitmap(image)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun sendFoodData() {
        val titleField: EditText = findViewById(R.id.edit_foodName)
        val imageField: ImageView = findViewById(R.id.edit_foodImage)
        val descriptionField: EditText = findViewById(R.id.edit_foodDescription)
        val userNameField: EditText = findViewById(R.id.edit_userName)
        val userAddressField: EditText = findViewById(R.id.edit_userAddress)
        var isEntered = titleField.text.toString().isNotEmpty()
        isEntered = isEntered && descriptionField.text.toString().isNotEmpty()
        isEntered = isEntered && userNameField.text.toString().isNotEmpty()
        isEntered = isEntered && userAddressField.text.toString().isNotEmpty()
        if ( isEntered ) {
            val titleText = titleField.text.toString()
            val imageBitmap = (imageField.drawable as BitmapDrawable).bitmap
            val descriptionText = descriptionField.text.toString()
            val userNameText = userNameField.text.toString()
            val userAddressText = userAddressField.text.toString()

            val randomName = List(25) { ( ('a'..'z') + ('A'..'Z') + ('0'..'9') ).random() }.joinToString("")
            val storageRef = storage.reference
            val imageText = "$randomName.jpg"
            val mountainsRef = storageRef.child(imageText)

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = mountainsRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Log.w("UPLOAD", "error")
            }.addOnSuccessListener {
                // 遷移
                val foodDelivery = FoodDeliveryDatabaseController()
                foodDelivery.upload(titleText, imageText, descriptionText, userNameText, userAddressText)
                Log.d("DEMO", "成功")
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }

}
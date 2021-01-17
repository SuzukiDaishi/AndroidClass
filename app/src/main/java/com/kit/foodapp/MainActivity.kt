package com.kit.foodapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.InputStream

@GlideModule
class AppGlideModule : AppGlideModule(){
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.append(StorageReference::class.java, InputStream::class.java, FirebaseImageLoader.Factory())
    }
}

class FoodsAdapter(private val context: Context, private val foodMap: Map<String, FoodDeliveryData>) : RecyclerView.Adapter<FoodsAdapter.FoodsHolder>() {

    private val strage: FirebaseStorage = Firebase.storage

    lateinit var listener: OnItemClickListener

    class FoodsHolder(view: View): RecyclerView.ViewHolder(view) {
        val foodImageView: ImageView = view.findViewById(R.id.foodIcon)
        val foodTitleView: TextView = view.findViewById(R.id.foodTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.list_item, parent, false)
        return FoodsHolder(item)
    }

    override fun getItemCount(): Int = foodMap.size

    override fun onBindViewHolder(holder: FoodsHolder, position: Int) {
        val key = ArrayList(foodMap.keys)[position]
        val imageRef = strage.getReference(foodMap[key]!!.imagePath.toString())
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                    .load(uri.toString())
                    .into(holder.foodImageView)
        }
        holder.foodTitleView.text = foodMap[key]!!.title
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(it, position, foodMap[key]!!, key)
        }
    }

    interface OnItemClickListener{
        fun onItemClickListener(view: View, position: Int, food: FoodDeliveryData, foodKey: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.toFoodButton)
        button.setOnClickListener {
            startActivity(Intent(this, FoodActivity::class.java))
        }

        val foodDelivery = FoodDeliveryDatabaseController()
        foodDelivery.download { foodList: Map<String, FoodDeliveryData> ->
            findViewById<RecyclerView>(R.id.recyclerView).also { recyclerView: RecyclerView ->
                val adapter = FoodsAdapter(this, foodList)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
                adapter.setOnItemClickListener(
                        object: FoodsAdapter.OnItemClickListener {
                            override fun onItemClickListener(view: View, position: Int, food: FoodDeliveryData, foodKey: String) {
                                Log.d("DEMO", food.title.toString())
                                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                                intent.putExtra("foodKey", foodKey)
                                intent.putExtra("title", food.title)
                                intent.putExtra("imagePath", food.imagePath)
                                intent.putExtra("description", food.description)
                                intent.putExtra("userName", food.userName)
                                intent.putExtra("userAddress", food.userAddress)
                                this@MainActivity.startActivity(intent)
                            }
                        }
                )
            }
        }

    }


}
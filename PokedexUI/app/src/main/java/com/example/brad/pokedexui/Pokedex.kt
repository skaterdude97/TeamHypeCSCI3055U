package com.example.brad.pokedexui


import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.ImageView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.onClick

class Pokedex : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_pokedex)

        val search = findViewById(R.id.searchButton) as Button
        val favButton = findViewById(R.id.favoritesButton) as Button
        //val createButoon = findViewById(R.id.createButton) as Button



        search.onClick { startActivity(intentFor<Search>()) }
        favButton.onClick { startActivity(intentFor<Favorites>())}
        //createButoon.onClick { startActivity(intentFor<CreateTeam>()) }
    }

}

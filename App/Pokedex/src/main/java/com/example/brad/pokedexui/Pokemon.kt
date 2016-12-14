package com.example.brad.pokedexui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import java.net.URL
import org.jetbrains.anko.db.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*

class Pokemon : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pokemon)

        val id = this.intent.extras.get("ID").toString().toInt()
        var pokemon : me.sargunvohra.lib.pokekotlin.model.Pokemon
        var spriteUrl : URL
        var spriteArray: List<String?>
        var spriteCounter: Int
        var spriteBMP : Bitmap = BitmapFactory.decodeResource(this.baseContext.resources,R.drawable.no_image_available)
        val sprite = findViewById(R.id.pokemon_sprite) as ImageView
        val nameText = findViewById(R.id.pokemon_name) as TextView
        val favButton = findViewById(R.id.fav_button) as Button
        val descriptionText = findViewById(R.id.pokemon_description) as TextView

        val db = FavDatabaseOpenHelper.getInstance(this.applicationContext).writableDatabase
        var isFav : Boolean? = null

        doAsync {
            val getFavStatus = db.select("Pokemon").where("id = " + id).parseList(rowParser {
                name: String, id: Int -> PokemonRef(name, id) }).isNotEmpty()
            uiThread {
                isFav = getFavStatus
                if (getFavStatus)
                    favButton.setText("Remove From Favorites")
            }
        }

        favButton.onClick { toast("still loading dude") }
        descriptionText.setText("Loading...")
        nameText.setText("LOADING")


        doAsync {
            val pokeRef = getPokemon(id)
            spriteArray = arrayOf(pokeRef.sprites.frontDefault, pokeRef.sprites.backDefault,
                    pokeRef.sprites.frontFemale, pokeRef.sprites.backFemale,
                    pokeRef.sprites.frontShiny, pokeRef.sprites.backShiny,
                    pokeRef.sprites.frontShinyFemale, pokeRef.sprites.backShinyFemale).
                    filter{ sprite -> sprite!=null }
            spriteCounter = 0
            if (spriteArray.isNotEmpty()) {
                spriteUrl = URL(spriteArray[spriteCounter])
                spriteBMP = BitmapFactory.decodeStream(spriteUrl.openConnection().inputStream)
            }
            uiThread {

                pokemon = pokeRef
                nameText.text = pokemon.name[0].toUpperCase() + pokemon.name.substring(1)


                sprite.setImageBitmap(spriteBMP)
                var info = "Id: " + pokemon.id.toString() + "\nHeight: " + pokemon.height.toString() +
                        "\nWeight: " + pokemon.weight.toString() + "\nTypes: "
                for (type in pokemon.types) {
                    info += type.type.name[0].toUpperCase() + type.type.name.substring(1) + " "
                }
                descriptionText.text = info

                favButton.onClick {
                    if (isFav!=null && !isFav!!) {
                        db.insert("Pokemon", "name" to pokemon.name, "id" to pokemon.id)
                        favButton.setText("Remove From Favorites")
                        isFav = true
                    } else {
                        db.delete("Pokemon", "id = " + id.toString(), null)
                        favButton.setText("Add To Favorites")
                        isFav = false
                    }
                }

                sprite.onClick {
                    if (spriteArray.size > 1){
                        if (spriteCounter < spriteArray.size-1) {
                            spriteCounter++
                        } else {
                            spriteCounter = 0
                        }
                        doAsync { spriteBMP = BitmapFactory.decodeStream(URL(
                                spriteArray[spriteCounter]).openConnection().inputStream)
                            uiThread {
                                sprite.setImageBitmap(spriteBMP)
                            }
                        }
                    }
                }
            }
        }





    }

    fun getPokemon(id : Int) : me.sargunvohra.lib.pokekotlin.model.Pokemon {
        val pokeApi = PokeApiClient()
        return pokeApi.getPokemon(id)
    }
}





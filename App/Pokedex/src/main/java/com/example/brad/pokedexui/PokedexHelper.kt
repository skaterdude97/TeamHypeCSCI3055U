package com.example.brad.pokedexui

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.db.*
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textView

/**
 * Created by andrei on 12/13/16.
 */

class FavAdapter(val activity : AppCompatActivity) : BaseAdapter() {
    var pokeList : List<PokemonRef> = arrayListOf()
    override fun getView(i : Int, v : View?, parent : ViewGroup?) : View {
        val item = getItem(i)
        return with(parent!!.context) {
            relativeLayout {
                textView(Character.toUpperCase(item.name[0])+item.name.substring(1)) {
                    textSize = 32f
                }
            }
        }
    }

    override fun getItem(position : Int) : PokemonRef {
        return pokeList.get(position)
    }

    override fun getCount() : Int {
        return pokeList.size
    }

    override fun getItemId(position : Int) : Long {
        return getItem(position).id.toLong()
    }

    fun getList() {
        pokeList = FavDatabaseOpenHelper.getInstance(activity.applicationContext).
                readableDatabase.select("Pokemon").parseList(rowParser {
            name : String, id: Int->  PokemonRef(name, id)})
    }

}

class FavDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx,
        FavDatabaseOpenHelper.DB_NAME, null, FavDatabaseOpenHelper.DB_Version) {
    companion object {
        val DB_NAME = "favorites.db"
        val DB_Version = 1
        private var instance: FavDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): FavDatabaseOpenHelper {
            if (instance == null) {
                instance = FavDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("Pokemon", true,
                "name" to TEXT,
                "id" to INTEGER + PRIMARY_KEY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("Pokemon", true)
        onCreate(db)
    }
}

class MyAdapter(val activity : Search) : BaseAdapter() {
    var list : List<PokemonRef> = arrayListOf(PokemonRef("LOADING", 0))
    var search: List<PokemonRef> = list

    override fun getView(i : Int, v : View?, parent : ViewGroup?) : View {
        val item = getItem(i)
        return with(parent!!.context) {
            relativeLayout {
                textView(Character.toUpperCase(item.name[0])+item.name.substring(1)) {
                    textSize = 32f
                }
                textView(item.id.toString()) {
                    textSize = 20f
                }.lparams {
                    alignParentBottom()
                    alignParentRight()
                }
            }
        }
    }

    override fun getItem(position : Int) : PokemonRef {
        return search.get(position)
    }

    override fun getCount() : Int {
        return search.size
    }

    override fun getItemId(position : Int) : Long {
        return getItem(position).id.toLong()
    }

    fun getData()  {
        val pokeApi = PokeApiClient()
        list = pokeApi.getPokemonList(0,721).results.map { p -> PokemonRef(p.name,p.id) }
        search = list
    }

}

data class PokemonRef (val name: String, val id: Int)
package com.example.brad.pokedexui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import kotlinx.android.synthetic.main.favorites_page.*
import me.sargunvohra.lib.pokekotlin.model.NamedApiResourceList

import org.jetbrains.anko.*
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select

class Favorites : AppCompatActivity() {

    var list : MutableList<FavAdapter> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites_page)
        val pokeAdapter = FavAdapter(this)
        pokeAdapter.getList()
        list.add(pokeAdapter)

        val addButton = findViewById(R.id.add_button) as ImageButton



        val nameText = findViewById(R.id.name_text) as EditText
        nameText.hint = "Enter Pokemon Name"
        (nameText as TextView).setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1.equals(EditorInfo.IME_ACTION_DONE)){
                    addButton.performClick()
                    return true
                }
                return false
            }
        })
//        nameText.setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
//                toast(p1.toString())
//                return true
//            }
//        })

        addButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(intentFor<Search>("name" to nameText.text))
                //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        val favView = findViewById(R.id.fav_view) as ListView
        favView.adapter = pokeAdapter
        favView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                startActivity(intentFor<Pokemon>("ID" to pokeAdapter.getItem(p2).id))
                //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }


    }

    override fun onResume() {
        super.onResume()
        if (list.isNotEmpty()) {
            list[0].getList()
            list[0].notifyDataSetInvalidated()
       }
    }

}

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
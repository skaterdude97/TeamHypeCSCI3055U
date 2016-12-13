package com.example.brad.pokedexui



import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import org.jetbrains.anko.*
import kotlin.collections.filter

class Search : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_page)
        var searchText : String = ""
        if (this.intent.hasExtra("name"))
            searchText = this.intent.extras.get("name").toString()
        val mAdapter = MyAdapter(this);
        val searchbox = findViewById(R.id.searchView) as SearchView

        //        searchbox.onQueryTextListener {
//            onQueryTextChange { x ->
//                mAdapter.search = mAdapter.list.filter { x -> x.name.contains(x.toString()) }
//                if (x.isNullOrBlank()){
//                    mAdapter.search = mAdapter.list
//                }
//                false
//            }
//            onQueryTextSubmit { x ->
//                mAdapter.search = mAdapter.list.filter { x ->
//                    x.name.contains(x.toString())
//                }
//                if (x.isNullOrBlank()){
//                    mAdapter.search = mAdapter.list
//                }
//                false
//            }
//        }



        searchbox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                return operateSearch(p0.toString())
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchbox.clearFocus()
                return operateSearch(p0.toString())

            }

            fun operateSearch (p0: String) : Boolean {
                if (p0.isNullOrBlank()) {
                    mAdapter.search = mAdapter.list!!
                    mAdapter.notifyDataSetChanged()
                    return true
                } else {
                    val sorted = mAdapter.list!!.filter {
                        x ->
                        x.name.contains(p0.toString().trim().toLowerCase())
                    }.sortedBy { p -> p.name }
                    mAdapter.search = sorted.filter {
                        p ->
                        p.name[0] == p0.toString()[0].toLowerCase()
                    } + sorted.filter {
                        p ->
                        p.name[0] != p0.toString()[0].toLowerCase()
                    }
                    mAdapter.notifyDataSetChanged()
                    return true
                }

            }

        })
        searchbox.queryHint = "Search for Pokemon"
        if (!searchText.isNullOrEmpty()) {
            searchbox.setQuery(searchText, true)
            searchbox.isIconified = false
        }
        doAsync {
            mAdapter.getData()
            uiThread {
                searchbox.setQuery(searchbox.query,true)
                mAdapter.notifyDataSetChanged()

            }
        }

        val searchResult = findViewById(R.id.SearchResults) as ListView
        searchResult.adapter = mAdapter
        searchResult.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                startActivity(intentFor<Pokemon>("ID" to mAdapter.getItem(p2).id))
                // /throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }



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
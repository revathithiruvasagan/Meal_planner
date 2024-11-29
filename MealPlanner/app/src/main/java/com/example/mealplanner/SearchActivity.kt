package com.example.mealplanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.mealplanner.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var rvAdapter: SearchAdapter
    private lateinit var dataList: ArrayList<Recipe>
    private lateinit var recipes: List<Recipe>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.search.requestFocus()

        val db = Room.databaseBuilder(this, AppDatabase::class.java, "db_name")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .createFromAsset("recipe.db")
            .build()
        val daoObject = db.getDao()
        recipes = daoObject.getAll()?.filterNotNull() ?: emptyList()

        setUpRecyclerView()

        binding.goBackHome.setOnClickListener{finish()}
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    filterData(s.toString())
                } else {
                    setUpRecyclerView()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        binding.rvSearch.setOnTouchListener { v, event ->
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }

    private fun filterData(filterText: String) {
        val filterData = ArrayList<Recipe>()
        for (recipe in recipes) {
            if (recipe.tittle.lowercase().contains(filterText.lowercase())) {
                filterData.add(recipe)
            }
        }
        rvAdapter.filterList(filterList = filterData)
    }

    private fun setUpRecyclerView() {
        dataList = ArrayList()
        binding.rvSearch.layoutManager = LinearLayoutManager(this)

        for (recipe in recipes) {
            if (recipe.category.contains("Popular")) {
                dataList.add(recipe)
            }
        }

        rvAdapter = SearchAdapter(dataList, this)
        binding.rvSearch.adapter = rvAdapter
    }
}

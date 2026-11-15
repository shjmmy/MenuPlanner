package com.example.menuplanner

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val mealList = mutableListOf<String>()
    private val shopList = mutableListOf<String>()

    private lateinit var menuAdapter: ArrayAdapter<String>
    private lateinit var shopAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnTabMenu = findViewById<Button>(R.id.btnTabMenu)
        val btnTabShop = findViewById<Button>(R.id.btnTabShop)
        val layoutMenu = findViewById<LinearLayout>(R.id.layoutMenu)
        val layoutShop = findViewById<LinearLayout>(R.id.layoutShop)

        val spinnerDay = findViewById<Spinner>(R.id.spinnerDay)
        val spinnerMealType = findViewById<Spinner>(R.id.spinnerMealType)
        val editDish = findViewById<EditText>(R.id.editDish)
        val editIngredients = findViewById<EditText>(R.id.editIngredients)
        val btnAddMeal = findViewById<Button>(R.id.btnAddMeal)
        val listMenu = findViewById<ListView>(R.id.listMenu)

        val editProduct = findViewById<EditText>(R.id.editProduct)
        val btnAddProduct = findViewById<Button>(R.id.btnAddProduct)
        val listShop = findViewById<ListView>(R.id.listShop)
        val btnClearBought = findViewById<Button>(R.id.btnClearBought)

        val days = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
        val mealTypes = arrayOf("Завтрак", "Обед", "Ужин")

        spinnerDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)
        spinnerMealType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mealTypes)

        loadData()

        menuAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mealList)
        listMenu.adapter = menuAdapter

        shopAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, shopList)
        listShop.adapter = shopAdapter

        btnTabMenu.setOnClickListener {
            layoutMenu.visibility = View.VISIBLE
            layoutShop.visibility = View.GONE
            btnTabMenu.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2E7D32"))
            btnTabShop.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#757575"))
        }

        btnTabShop.setOnClickListener {
            layoutMenu.visibility = View.GONE
            layoutShop.visibility = View.VISIBLE
            btnTabShop.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2E7D32"))
            btnTabMenu.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#757575"))
        }

        btnAddMeal.setOnClickListener {
            val dish = editDish.text.toString().trim()
            val ingr = editIngredients.text.toString().trim()
            if (dish.isEmpty()) {
                Toast.makeText(this, "Введите название блюда", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val day = spinnerDay.selectedItem.toString()
            val type = spinnerMealType.selectedItem.toString()

            mealList.add("$day ($type): $dish")
            menuAdapter.notifyDataSetChanged()
            editDish.text.clear()

            if (ingr.isNotEmpty()) {
                val items = ingr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                for (item in items) {
                    val capitalized = item.replaceFirstChar { it.uppercase() }
                    if (!shopList.contains(capitalized)) {
                        shopList.add(capitalized)
                    }
                }
                shopAdapter.notifyDataSetChanged()
                editIngredients.text.clear()
                Toast.makeText(this, "Блюдо и продукты добавлены!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Блюдо добавлено в меню", Toast.LENGTH_SHORT).show()
            }
            saveData()
        }

        listMenu.setOnItemLongClickListener { _, _, position, _ ->
            mealList.removeAt(position)
            menuAdapter.notifyDataSetChanged()
            saveData()
            Toast.makeText(this, "Удалено из меню", Toast.LENGTH_SHORT).show()
            true
        }

        btnAddProduct.setOnClickListener {
            val prod = editProduct.text.toString().trim()
            if (prod.isNotEmpty()) {
                shopList.add(prod.replaceFirstChar { it.uppercase() })
                shopAdapter.notifyDataSetChanged()
                editProduct.text.clear()
                saveData()
            }
        }

        btnClearBought.setOnClickListener {
            val checked = listShop.checkedItemPositions
            for (i in shopList.size - 1 downTo 0) {
                if (checked.get(i)) {
                    shopList.removeAt(i)
                }
            }
            listShop.clearChoices()
            shopAdapter.notifyDataSetChanged()
            saveData()
            Toast.makeText(this, "Отмеченные продукты удалены", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData() {
        val prefs = getSharedPreferences("menu_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("meals", mealList.joinToString("\n"))
            .putString("shop", shopList.joinToString("\n"))
            .putBoolean("initialized", true)
            .apply()
    }

    private fun loadData() {
        val prefs = getSharedPreferences("menu_prefs", Context.MODE_PRIVATE)
        val initialized = prefs.getBoolean("initialized", false)
        if (!initialized) {
            mealList.addAll(
                listOf(
                    "Понедельник (Завтрак): Овсянка с бананом",
                    "Понедельник (Ужин): Куриное филе с рисом",
                    "Вторник (Обед): Паста карбонара",
                    "Среда (Ужин): Запеченная рыба с овощами"
                )
            )
            shopList.addAll(
                listOf(
                    "Овсяные хлопья",
                    "Бананы",
                    "Куриное филе",
                    "Рис",
                    "Спагетти",
                    "Сливки 20%",
                    "Бекон"
                )
            )
            saveData()
        } else {
            val mealsStr = prefs.getString("meals", "") ?: ""
            val shopStr = prefs.getString("shop", "") ?: ""
            if (mealsStr.isNotEmpty()) mealList.addAll(mealsStr.split("\n"))
            if (shopStr.isNotEmpty()) shopList.addAll(shopStr.split("\n"))
        }
    }
}

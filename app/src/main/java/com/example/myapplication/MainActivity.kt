package com.example.myapplication

//This is a total disaster :( eh

import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fieldsAdapter: FieldsAdapter

    private lateinit var formValues: MutableMap<Int, String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        formValues = mutableMapOf()

        setupRecyclerView()




        val serverResponse = getFormValues()
        fieldsAdapter.fields = serverResponse.flatten()


        val fieldsList = mutableListOf<List<Fields>>()

        try {
            val obj = JSONObject(getJSONFromAssets()!!)
            val fieldsArray = obj.getJSONArray("fields")

            for (i in 0 until fieldsArray.length()) {
                val fieldList = mutableListOf<Fields>()
                val jsonArray = fieldsArray.getJSONArray(i)

                for (j in 0 until jsonArray.length()) {
                    val fieldObject = jsonArray.getJSONObject(j)
                    val field = Fields(
                        fieldObject.getInt("field_id"),
                        fieldObject.getString("hint"),
                        fieldObject.getString("field_type"),
                        fieldObject.getString("keyboard"),
                        fieldObject.getBoolean("required"),
                        fieldObject.getBoolean("is_active"),
                        fieldObject.getString("icon")
                    )
                    fieldList.add(field)
                }

                fieldsList.add(fieldList)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.btnRegister.setOnClickListener {
            if (validateFields()) {
                performRegistration()
            } else {
                d("Registration", "Validation failed. Please fill in all required fields.")
            }
        }

    }

    private fun validateFields(): Boolean {
        for (field in fieldsAdapter.fields) {
            if (field.required && formValues[field.fieldId].isNullOrEmpty()) {
                Toast.makeText(this, "${field.hint} is required.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun performRegistration() {
        for ((fieldId, value) in formValues) {
            d("Registration", "Field $fieldId: $value")
        }
        d("Registration", "Registration successful!")

        formValues.clear()

    }

    private fun getFormValues(): List<List<Fields>> {
        return listOf(
            listOf(
                Fields(1, "UserName", "input", "text", false, true, ""),
                Fields(2, "Email", "input", "text", true, true, ""),
                Fields(3, "Phone", "input", "number", true, true, "")
            ),
            listOf(
                Fields(4, "FullName", "input", "text", true, true, ""),
                Fields(5, "Country", "input", "text", false, true, ""),
                Fields(6, "Birthday", "chooser", "text", false, true, ""),
                Fields(7, "Gender", "chooser", "text", false, true, "")
            )
        )
    }


    private fun setupRecyclerView() = binding.rvFields.apply {
        fieldsAdapter = FieldsAdapter(this@MainActivity, formValues)
        adapter = fieldsAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)

    }


    private fun getJSONFromAssets(): String? {

        var json: String? = null
        val charset: Charset = Charsets.UTF_8

        try {
            val myUsersJSONFile = assets.open("fields.json")
            val size = myUsersJSONFile.available()
            val buffer = ByteArray(size)
            myUsersJSONFile.read(buffer)
            myUsersJSONFile.close()
            json = String(buffer, charset)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("JSONParsingError", "Error parsing JSON: ${e.message}")

        }
        return json
    }
}
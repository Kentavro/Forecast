package com.alexhey.newweatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.alexhey.newweatherapp.data.WeatherModel
import com.alexhey.newweatherapp.screens.DialogSearch
import com.alexhey.newweatherapp.screens.MainCard
import com.alexhey.newweatherapp.screens.TabLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val daysList = remember {
                mutableStateOf(listOf<WeatherModel>())
            }
            val dialogState = remember {
                mutableStateOf(false)
            }
            val currentDay = remember {
                mutableStateOf(WeatherModel(
                    "",
                    "",
                    "10.0",
                    "",
                    "",
                    "10.0",
                    "10.0",
                    ""
                ))
            }
            if(dialogState.value) {
                DialogSearch(dialogState, onSubmit = {
                    getData(it,this, daysList, currentDay)
                })
            }
            getData("Kazan",this, daysList, currentDay)
            Image(
                painter = painterResource(id = R.drawable.weather_bg),
                contentDescription = "Weather Image with the clouds",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(1f),
                contentScale = ContentScale.Crop
            )
            Column() {
                MainCard(currentDay, onClickSync = {
                    getData("Kazan",this@MainActivity, daysList, currentDay)
                }, onClickSearch = {
                    dialogState.value = true
                })
                TabLayout(daysList, currentDay)
            }
        }
    }
}

private fun getData(city: String, context: Context, daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
            "a7bf884d85114bc6894194901241207" +
            "&q=$city" +
            "&days=" +
            "10" +
            "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
            response->
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list
        },
        {
            Log.d("MyLog","Volley Error: $it")
        }
    )
    queue.add(sRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")

    )
    return list
}
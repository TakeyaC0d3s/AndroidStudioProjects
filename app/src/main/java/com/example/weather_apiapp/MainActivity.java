package com.example.weather_apiapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Variables
    Button btn_cityID, btn_getWeatherByID, btn_getWeatherByName;
    EditText et_dataInput;
    ListView lv_weatherReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign values to each control variable on the layout by using 'find' methods
        btn_cityID = findViewById(R.id.btn_getCityID);
        btn_getWeatherByID = findViewById(R.id.btn_getWeatherByCItyID);
        btn_getWeatherByName = findViewById(R.id.btn_getWeatherByCityName);

        et_dataInput = findViewById(R.id.et_dataInput);
        lv_weatherReports = findViewById(R.id.lv_weatherReports);
        //Create Weather Data Service object
        final WeatherDataService weatherDataService = new WeatherDataService(MainActivity.this);

        //Click listener for each button
        btn_cityID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CALLBACK with getCityID method: TO get this value we cant return it as String we have tro wait for background process to run
                weatherDataService.getCityID(et_dataInput.getText().toString(), new WeatherDataService.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String cityID) {
                        Toast.makeText(MainActivity.this, "Return an ID of " + cityID, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btn_getWeatherByID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                weatherDataService.getCityForecastByID(et_dataInput.getText().toString(), new WeatherDataService.ForecastByIDResponse() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        //Toast.makeText(MainActivity.this, weatherReportModels.toString(), Toast.LENGTH_SHORT).show();

                        //put entire list into the listview control
                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
                        lv_weatherReports.setAdapter(arrayAdapter);
                    }
                });
                //CALLBACK with getCityID method: TO get this value we cant return it as String we have tro wait for background process to run
//                weatherDataService.getCityForecastByID(et_dataInput.getText().toString(), new WeatherDataService.VolleyResponseListener() {
//                    @Override
//                    public void onError(String message) {
//                        Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(String cityID) {
//                        Toast.makeText(MainActivity.this, "Return an ID of " + cityID, Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }


          }
        });

        btn_getWeatherByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                weatherDataService.getCityForecastByName(et_dataInput.getText().toString(), new WeatherDataService.GetCityForecastByNameCallback() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        //Toast.makeText(MainActivity.this, weatherReportModels.toString(), Toast.LENGTH_SHORT).show();

                        //put entire list into the listview control
                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
                        lv_weatherReports.setAdapter(arrayAdapter);
                    }
                });
                //Toast.makeText(MainActivity.this,"You typed " + et_dataInput.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
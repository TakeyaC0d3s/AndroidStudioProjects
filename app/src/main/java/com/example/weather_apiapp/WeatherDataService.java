package com.example.weather_apiapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {
    //Declare class variables
    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";
    Context context;
    String cityID;

    //Constructor taking in Context parameter
    public WeatherDataService(Context context) {
        this.context = context;
    }
    //Solution to Asynchronous problem: Initiate VolleyResponseListener interface.
    // //IT is specific to getCityID method so add it as method parameter
    public interface VolleyResponseListener {
        void onError(String message);

        //Expected 'Object response' is  a String called 'cityID'
        void onResponse(String cityID);
    }


    //--------------------BUTTON METHODS---------------------------//
    //getCityID method with String and VolleyResponseListener
    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener) {
        // Instantiate the constant RequestQueue
        String url = QUERY_FOR_CITY_ID + cityName;

        // -------Request a JsonArray response from the provided URL. -------(BACKGROUND REQUEST)------------//
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response){
                cityID = "";
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Display message when button is clicked////-----THIS WORKED BUT DIDN'T RETURN ID # to MainActivity------///
                //Toast.makeText(context, "City ID = " + cityID, Toast.LENGTH_SHORT).show();

                //Call volleyResponseListener and onResponse method as defined in interface
                volleyResponseListener.onResponse(cityID);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error message
                //Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();

                volleyResponseListener.onError("Something wrong");
            }
        });
        // Add the request to the RequestQueue.
        //queue.add(request);

        //*REFACTORED* Instantiate RequestQueue and add request to RequestQue in one statement
        MySingleton.getInstance(context).addToRequestQueue(request);
        
        //Asynchronous problem! returns a null.
        //!!!!!JsonArrayRequest and MySingleton instantiation was skipped while using background process
        //While waiting for requests/processes to be returned the variable 'cityID' is never defined.
        //SOLUTION #1---Implement callback /**Callback is a way to schedule a method call occurrence when another method completes its task**/
        //return cityID;

    }


    public interface ForecastByIDResponse {
        void onError(String message);

        //Expected 'Object response' is  a String called 'cityID'
        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

   public void getCityForecastByID(String cityID, ForecastByIDResponse forecastByIDResponse) {

       List<WeatherReportModel> weatherReportModels = new ArrayList<>();
       String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;

       //get the json object
       JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

           @Override
           public void onResponse(JSONObject response) {
                //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

               try {
                   //get the property called "consolidated_weather"
                   JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");

                   //get first item in array
                   //get each item in the array and assign to new WeatherReportModel object

                   //Loop through Json array by length of list =: 5day forecast
                   for(int i = 0 ; i < consolidated_weather_list.length() ; i++) {
                       WeatherReportModel one_day_weather = new WeatherReportModel();
                       JSONObject first_day_from_API = (JSONObject) consolidated_weather_list.get(i);

                       one_day_weather.setId(first_day_from_API.getInt("id"));
                       one_day_weather.setWeather_state_name(first_day_from_API.getString("weather_state_name"));
                       one_day_weather.setWeather_state_abbr(first_day_from_API.getString("weather_state_abbr"));
                       one_day_weather.setWind_direction_compass(first_day_from_API.getString("wind_direction_compass"));
                       one_day_weather.setCreated(first_day_from_API.getString("created"));
                       one_day_weather.setApplicable_date(first_day_from_API.getString("applicable_date"));
                       one_day_weather.setMin_temp(first_day_from_API.getLong("min_temp"));
                       one_day_weather.setMax_temp(first_day_from_API.getLong("max_temp"));
                       one_day_weather.setThe_temp(first_day_from_API.getLong("the_temp"));
                       one_day_weather.setWind_speed(first_day_from_API.getLong("wind_speed"));
                       one_day_weather.setWind_direction(first_day_from_API.getLong("wind_direction"));
                       one_day_weather.setAir_pressure(first_day_from_API.getInt("air_pressure"));
                       one_day_weather.setHumidity(first_day_from_API.getInt("humidity"));
                       one_day_weather.setVisibility(first_day_from_API.getLong("visibility"));
                       one_day_weather.setPredictability(first_day_from_API.getInt("predictability"));
                       weatherReportModels.add(one_day_weather);
                   }
                   //-------CALLBACK------
                    forecastByIDResponse.onResponse(weatherReportModels);


               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               
           }
       });

       MySingleton.getInstance(context).addToRequestQueue(request);

   }

   public interface GetCityForecastByNameCallback {
        void onError(String message);
        void onResponse(List<WeatherReportModel> weatherReportModels);
   }

    public void getCityForecastByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback) {
        //fetch the city id given the city name
        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityID) {
                //now we have city id
                getCityForecastByID(cityID, new ForecastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse (List<WeatherReportModel> weatherReportModels) {
                        //we have weather report
                            getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });

        //fetch the city forecast if we have city id

    }
}

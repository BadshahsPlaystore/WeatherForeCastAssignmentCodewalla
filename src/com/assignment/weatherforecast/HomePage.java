package com.assignment.weatherforecast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class HomePage extends Activity {

	GridView gridViewForeCast;
	ArrayList<Bitmap> icon;
	ArrayList<String>  description ,date, maxTemp,  minTemp, windSpeed, tempDay, tempNight, tempEvening, tempMorning, humidity, clouds;
	public static final String APPI = "e60610d9df77e2c6862214a8fc6e42c2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		init();
		
		//-------------- get home button on action bar----------------------
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowCustomEnabled(true);
		
		//-------------- run async task to fetch data ----------------------
		new GetWeatherInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Pune");
		
		//----------------- dialog popup on grid item click-----------------
		gridViewForeCast.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				final Dialog dialog = new Dialog(HomePage.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.grid_item_on_click_popup);

				//-------------------------------- get reference to views----------------
				TextView descriptionPopUp = (TextView)dialog.findViewById(R.id.textViewDescriptionCustomGridPopUp);
				TextView tempMaxPopUp = (TextView) dialog.findViewById(R.id.textViewValueTempMaxPopUp);
				TextView tempMinPopUp = (TextView) dialog.findViewById(R.id.textViewValueTempMinPopUp);
				TextView tempDayPopUp = (TextView)dialog.findViewById(R.id.textViewValueTempDayPopUp);
				TextView tempNightPopUp = (TextView) dialog.findViewById(R.id.textViewValueTempNightPopUp);
				TextView tempMornPopUp = (TextView) dialog.findViewById(R.id.textViewValueTempMornPopUp);
				TextView tempEvePopUp = (TextView)dialog.findViewById(R.id.textViewValueTempEvePopUp);
				TextView HumidityTPopUp = (TextView) dialog.findViewById(R.id.textViewValueHumidityPopUp);
				TextView CloudsTPopUp = (TextView) dialog.findViewById(R.id.textViewValueCloudsPopUp);
				TextView windSpeedTPopUp = (TextView)dialog.findViewById(R.id.textViewValueWindSpeedPopUp);
				TextView DateTPopUp = (TextView) dialog.findViewById(R.id.textViewDateCustomGridPopUp);
				ImageView iconPPopUp = (ImageView)dialog.findViewById(R.id.imageViewIconCustomGridPopUP);
				
				//-------------------- display content texts to respective views---------------
				descriptionPopUp.setText(description.get(arg2));
				tempMaxPopUp.setText(maxTemp.get(arg2));
				tempMinPopUp.setText(minTemp.get(arg2));
				tempDayPopUp.setText(tempDay.get(arg2));
				tempNightPopUp.setText(tempNight.get(arg2));
				tempMornPopUp.setText(tempMorning.get(arg2));
				tempEvePopUp.setText(tempEvening.get(arg2));
				HumidityTPopUp.setText(humidity.get(arg2));
				CloudsTPopUp.setText(clouds.get(arg2));
				windSpeedTPopUp.setText(windSpeed.get(arg2));

				//-------------------- parse date to normal format -----------------------------
				long unixseconds = Long.parseLong(date.get(arg2));
				Date date= new Date(unixseconds * 1000L);
				SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT04"));
				String formatedDate = sdf.format(date);
				
				
				DateTPopUp.setText(formatedDate);
				iconPPopUp.setImageBitmap(icon.get(arg2));
				
				dialog.show();

			}
		});
	}

	private void init() {
		
		//----------------------- initiatialize array lists and get reference to grid view-----------------
		gridViewForeCast = (GridView)findViewById(R.id.gridViewForeCast);
		icon = new ArrayList<Bitmap>();
		description = new ArrayList<String>();
		date = new ArrayList<String>();
		maxTemp = new ArrayList<String>();
		minTemp = new ArrayList<String>();
		windSpeed = new ArrayList<String>();
		tempDay = new ArrayList<String>();
		tempEvening = new ArrayList<String>();
		tempMorning = new ArrayList<String>();
		tempNight = new ArrayList<String>();
		humidity = new ArrayList<String>();
		clouds = new ArrayList<String>();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	public class GetWeatherInfo extends AsyncTask<String, String, String>{

		ProgressDialog pd;
		public GetWeatherInfo() {
			
			//--------------------- show progress dialog while fetching data ----------------------
			pd= new ProgressDialog(HomePage.this);
			pd.setTitle("Weather Info Loading..!");
			
			pd.setMessage("Please wait..");
			pd.setCancelable(false);
			pd.show();
			//------------------------------------------------------------------------------------
		}




		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//----------------- set custom adapter to GridView--------------------------------------
			pd.dismiss();
			CustomAdapterForGridView cafgv = new CustomAdapterForGridView(HomePage.this, date, icon, description, maxTemp, minTemp, windSpeed);
			gridViewForeCast.setAdapter(cafgv);
			//----------------------------------------------------------------------------------------
		}




		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}




		@SuppressLint("SimpleDateFormat")
		@Override
		protected String doInBackground(String... arg0) {

			System.out.println(arg0[0]);

			HttpResponse response;

			// Form the main URI to fetch weather info----------------------
			StringBuilder urlString = new StringBuilder();
			urlString.append("http://api.openweathermap.org/data/2.5/forecast/daily");
			urlString.append("?q="+arg0[0]+"&mode=json&units=metric&cnt=10&APPID="+APPI);

			String mainUrl = urlString.toString();
			mainUrl=mainUrl.replaceAll(" ", "+");

			//-------------- creating default httpClient----------------------
			HttpClient httpClient = new DefaultHttpClient();

			//-------------- Sending request --------------------------------
			HttpGet httpGet = null;
			httpGet = new HttpGet(mainUrl);


			try {
				System.out.println("Executing New URL...");
				
				//----------------- executing new URL-----------------------
				response = httpClient.execute(httpGet);

				//----------------- Parsing HTTp Response ----------------
				HttpEntity entity = response.getEntity();
				String jsonResponseString = EntityUtils.toString(entity);


				try {
					JSONObject jsonObj = new JSONObject(jsonResponseString);

					JSONArray jsonArrayList = jsonObj.getJSONArray("list");
					Bitmap map = null;
					for(int i =0; i < jsonArrayList.length(); i++)
					{
						date.add(jsonArrayList.getJSONObject(i).getString("dt").toString());
						HomePage.this.minTemp.add(jsonArrayList.getJSONObject(i).getJSONObject("temp").getString("min").toString());
						HomePage.this.maxTemp.add(jsonArrayList.getJSONObject(i).getJSONObject("temp").getString("max").toString());
						description.add(jsonArrayList.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description").toString());
						windSpeed.add( jsonArrayList.getJSONObject(i).getString("speed").toString());

						//------------------ fetch image from URI build---------------
						String iconId = jsonArrayList.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon").toString();
						StringBuilder urlStringIcon = new StringBuilder();
						urlStringIcon.append("http://openweathermap.org/img/w/"+iconId+".png");

						String mainUrlIcon = urlStringIcon.toString();
						mainUrlIcon=mainUrlIcon.replaceAll(" ", "+");
						map = downloadImage(mainUrlIcon);
						icon.add(map);


						tempDay.add(jsonArrayList.getJSONObject(i).getJSONObject("temp").getString("day").toString());
						tempEvening.add(jsonArrayList.getJSONObject(i).getJSONObject("temp").getString("eve").toString());
						tempNight.add(jsonArrayList.getJSONObject(i).getJSONObject("temp").getString("night").toString());
						tempMorning.add(jsonArrayList.getJSONObject(i).getJSONObject("temp").getString("morn").toString());
						humidity.add(jsonArrayList.getJSONObject(i).getString("humidity").toString());
						clouds.add(jsonArrayList.getJSONObject(i).getString("clouds").toString());

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}catch (ClientProtocolException e) {
				// writing exception to log
				e.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			return null;
		}
		// get image bitmap from URI-----------------
		private Bitmap downloadImage(String mainUrlIcon) {
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize  =1;
			try{
				stream  = getHttpConnection(mainUrlIcon);
				bitmap = BitmapFactory.decodeStream(stream,null,bmOptions);
				stream.close();
			}catch(IOException e1){
				e1.printStackTrace();
			}
			return bitmap;
		}

		private InputStream getHttpConnection(String mainUrlIcon)throws IOException {

			InputStream stream  = null;
			URL url  =new URL(mainUrlIcon);
			URLConnection connection = url.openConnection();
			try{
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();

				if(httpConnection.getResponseCode() ==HttpsURLConnection.HTTP_OK){
					stream = httpConnection.getInputStream();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return stream;
		}

	}

	//------------------Supress Back Key Event---------------------
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
				// do something on back.
				Intent startMain = new Intent(Intent.ACTION_MAIN); 
				startMain.addCategory(Intent.CATEGORY_HOME); 
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				startActivity(startMain); 
				return true; 
			}
			return super.onKeyDown(keyCode, event);
		}

	



}

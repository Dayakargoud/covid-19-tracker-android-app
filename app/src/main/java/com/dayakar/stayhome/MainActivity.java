package com.dayakar.stayhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.dayakar.stayhome.Adapters.CategoryAdapter;
import com.dayakar.stayhome.Data.Case;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,NavigationView.OnNavigationItemSelectedListener{
    private static String LOG_TAG="MainActivity";
    private TextView mTotal,activeText,recoveredText,deceasedText,lastUpadtedText,
            mStateTotalText,mStateActiveText,mStateRecoveredText,mStateDeceasedText,mStateLastUpadted,mViewAllstates,tipsText;
    private ArrayList<Case> totalList=new ArrayList<>();
    private Spinner changeSpinner;
    private ArrayList<String> stateList=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ArrayList<String> titles=new ArrayList<>();
    private ArrayList<Integer> links=new ArrayList<>();
    private int index=0;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private String new_version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       setUPUI();
       addCtegoryItems();
       setTips();
       if(isConnectedtoInternet()){
           new BackgroundTask().execute("https://api.covid19india.org/data.json");
           checkLatestAppVersion();
       }
    }
    private void setUPUI(){
        mTotal=findViewById(R.id.totalcase_value);
        activeText=findViewById(R.id.active_Case_value);
        recoveredText=findViewById(R.id.recoveredcases_value_textView);
        deceasedText=findViewById(R.id.deceasedValuetextView);
        lastUpadtedText=findViewById(R.id.lastUpdated_textView);
        tipsText=findViewById(R.id.main_Tip);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mStateActiveText=findViewById(R.id.state_active_Case_value);
        mStateTotalText=findViewById(R.id.state_totalcase_value);
        mStateRecoveredText=findViewById(R.id.state_recoveredcases_value_textView);
        mStateDeceasedText=findViewById(R.id.state_deceasedValuetextView);
        mStateLastUpadted=findViewById(R.id.state_lastUpdated_textView);
        mViewAllstates=findViewById(R.id.seeAllTexiview);
        changeSpinner=findViewById(R.id.changeStateText);

        mRecyclerView= findViewById(R.id.category_recyclerView);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3,RecyclerView.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mDrawerLayout = findViewById(R.id.main_drawer);
        mNavigationView = findViewById(R.id.main_navigation);

        mNavigationView.bringToFront();
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setTitle(R.string.app_name);

        mNavigationView.setNavigationItemSelectedListener(this);

        mViewAllstates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent=new Intent(MainActivity.this,AllStatesDetailsActivity.class);
                intent.putExtra("data",totalList);
                startActivity(intent);
            }
        });

    }

    private void setDataToViews(){

        Case c=totalList.get(0);
        mTotal.setText(c.getConfirmed());
        activeText.setText(c.getActive());
        recoveredText.setText(c.getRecovered());
        deceasedText.setText(c.getDeaths());
        lastUpadtedText.setText("Last updated: "+timeStamp(c.getLastupdatedtime()));

        changeSpinner.setOnItemSelectedListener(this);
        // Creating adapter for spinner for states
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.spinneritem_textview, stateList);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        changeSpinner.setAdapter(dataAdapter);

        changeSpinner.setSelection(4);



    }

    private void setStateData(int position){
        mViewAllstates.setVisibility(View.VISIBLE);

        Case c1=totalList.get(position);
        mStateActiveText.setText(c1.getActive());
        mStateTotalText.setText(c1.getConfirmed());
        mStateRecoveredText.setText(c1.getRecovered());
        mStateDeceasedText.setText(c1.getDeaths());
        mStateLastUpadted.setText("Last updated:"+timeStamp(c1.getLastupdatedtime()));
       // mStateValueText.setText(c1.getState());

    }
    private boolean isConnectedtoInternet(){

        ConnectivityManager
                cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //checking connection Status if connected or not...
        if(activeNetwork != null &&  activeNetwork.isConnectedOrConnecting()) {
            return true;
        }else {
            mViewAllstates.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
           //do something here when not connected
            return false;


        }
    }
    private void setTips(){
        final String[] tips={"Plan and calculate your essential needs for the next three weeks and get only what is bare minimum needed.",
                " Lockdown means LOCKDOWN! Avoid going out unless absolutely necessary. Stay safe!",
                "Avoid going out during the lockdown. Help break the chain of spread.",
                "Going out to buy essentials? Social Distancing is KEY! Maintain 2 metres distance between each other in the line."

        };


     Random r=new Random();
     int ind=r.nextInt(3);
     tipsText.setText(tips[ind]);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.changeStateText) {

            setStateData(position);
        }


        }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_feedback: {
                item.setChecked(true);
                sendFeedback();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            case R.id.nav_share: {
                item.setChecked(true);
                shareApp();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;

            }
            case R.id.nav_about: {
                item.setChecked(true);
                 startActivity(new Intent(MainActivity.this, AboutActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);

                return true;

            }


    }
    return false;
    }




    public class BackgroundTask extends AsyncTask<String,Void,String> {


            String jsonResponse="";

            @Override
            protected String  doInBackground(String... urls) {
                String urldata=urls[0];
                    URL url=createURL(urls[0]);
                    try{

                        jsonResponse=makeHttpConnection(url);

                        try{
                            JSONObject root=new JSONObject(jsonResponse);
                            JSONArray jsonArray=root.getJSONArray("statewise");
                           for(int i=0;i<jsonArray.length();i++) {

                               JSONObject jsonObject = jsonArray.getJSONObject(i);

                               String total = jsonObject.getString("confirmed");
                               String active = jsonObject.getString("active");
                               String recovered = jsonObject.getString("recovered");
                               String deceased = jsonObject.getString("deaths");
                               String lastUpdateTime = jsonObject.getString("lastupdatedtime");
                               String state = jsonObject.getString("state");
                                stateList.add(state);
                               Case c = new Case(total, active, recovered, deceased, lastUpdateTime, state);

                               totalList.add(c);
                           }

                        }catch (JSONException e){
                            Log.e(LOG_TAG,"error while parsing json object");
                            return null;

                        }

                    }catch (IOException e){
                        e.printStackTrace();
                        return null;

                    }



                return null;
            }


            @Override
            protected void onPostExecute(String s) {
                if(totalList.size()!=0){
                setDataToViews();}



            }

            private String makeHttpConnection(URL url)throws IOException {



                String jsonResponse = "";
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;

                try{

                    urlConnection=(HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.connect();

                    inputStream=urlConnection.getInputStream();
                    jsonResponse=readFromInputStream(inputStream);



                }catch (NullPointerException e){
                    Log.e(LOG_TAG,e.getMessage());

                }
                catch (IOException e){
                    e.printStackTrace();

                }finally {
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(inputStream !=null){
                        inputStream.close();
                    }
                }

                return jsonResponse;
            }

            private String readFromInputStream(InputStream inputStream)throws IOException{
                StringBuilder output=new StringBuilder();
                if(inputStream !=null){

                    InputStreamReader inputStreamReader=new InputStreamReader(inputStream, Charset.forName("UTF-8"));

                    BufferedReader reader=new BufferedReader(inputStreamReader);
                    String line=reader.readLine();
                    while (line !=null){

                        output.append(line);
                        line=reader.readLine();
                    }


                }

                return output.toString();
            }

            private URL createURL(String stringURL){
                URL url=null;
                if(TextUtils.isEmpty(stringURL)){

                    Log.e(LOG_TAG,"Url is empty..");
                    return null;

                }
                try{
                    url=new URL(stringURL);


                }catch (MalformedURLException e){
                    return null;

                }
                return url;
            }



        }

    private CharSequence timeStamp(String time){
                             //27/03/2020 18:07:24
       // SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        try{
            long receivedtime=sdf.parse(time).getTime();
            long now=System.currentTimeMillis();
            CharSequence ago= DateUtils.getRelativeTimeSpanString(receivedtime,now,DateUtils.MINUTE_IN_MILLIS);
            return ago;
        }catch (Exception e){
            e.getMessage();
            return time;

        }
    }
    private void addCtegoryItems(){
       // titles.add("Covid-19 History");
        titles.add("Symptoms");
        titles.add("Precautions");
        titles.add("Suspect/Report");
        titles.add("World statistics");
        titles.add("Isolation wards");
        titles.add("Help Lines");
        titles.add("FAQs");
        titles.add("Ask on Whatsapp \nby WHO");

        links.add(R.drawable.symptoms);
        links.add(R.drawable.precautions);
        links.add(R.drawable.suspect);
        links.add(R.drawable.worldmap);
        links.add(R.drawable.hospital);
        links.add(R.drawable.tollfree);
        links.add(R.drawable.faq);
        links.add(R.drawable.whowhatsapp);

        CategoryAdapter adapter=new CategoryAdapter(this,titles,links);

        mRecyclerView.setAdapter(adapter);
    }
    private void shareApp(){

        String body="Get minute to minute live updates of incidents happening in country with StayHome app.Get it for free " +
                "at \n"+
                "https://play.google.com/store/apps/details?id="+getPackageName();

        String mimeType="text/plain";
        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle("Share App with: ")
                .setText(body)
                .startChooser();
    }

    private void sendFeedback() {

        String mailto = "mailto:timetableapp214@gmail.com" +
                "&subject=" + Uri.encode("Feedback about "+getPackageName()+" Application") +
                "&body=" + Uri.encode("write your valuable feedback ");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            e.getMessage();
            Toast.makeText(this, "No apps found to send email", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkLatestAppVersion(){

        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference().child("App/version");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new_version=dataSnapshot.child("latestVersion").getValue().toString().trim();
                compare_versions(new_version);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void compare_versions(String new_version){
        float latest=Float.parseFloat(new_version);
        String current= BuildConfig.VERSION_NAME;
        float curnt=Float.parseFloat(current);
        try{
            if(latest>curnt){
                showDialogForAppUpdate();
            }}catch (Exception e){
            Log.i("Main Activity",e.getMessage());
        }


    }
    private void showDialogForAppUpdate(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.ic_system_update_black_24dp);
        dialog.setTitle("Update Available");
        dialog.setMessage("Please update the app to newer version to get latest features");
        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openPlayStore();
            }
        });
        dialog.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private  void openPlayStore() {
        try {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
            startActivity(goToMarket);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }








}

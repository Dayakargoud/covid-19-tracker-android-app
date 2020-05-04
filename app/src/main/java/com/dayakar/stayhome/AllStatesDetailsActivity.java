package com.dayakar.stayhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.dayakar.stayhome.Adapters.StateRecyclerAdapter;
import com.dayakar.stayhome.Data.Case;
import com.dayakar.stayhome.R;

import java.util.ArrayList;

public class AllStatesDetailsActivity extends AppCompatActivity {
    private ArrayList<Case> alllist=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_states_details);
        alllist=(ArrayList<Case>)getIntent().getSerializableExtra("data");
          setUpUI();

    }
    private void setUpUI(){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("All States");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
        mProgressBar=findViewById(R.id.statesLoadingProgressBar);
        mRecyclerView=findViewById(R.id.allstateRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        StateRecyclerAdapter adapter=new StateRecyclerAdapter(this,alllist);


            mProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

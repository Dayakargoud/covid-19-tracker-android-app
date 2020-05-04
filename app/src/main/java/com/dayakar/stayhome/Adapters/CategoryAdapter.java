package com.dayakar.stayhome.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.dayakar.stayhome.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> titlesList=new ArrayList<>();
    private ArrayList<Integer> linksList=new ArrayList<>();

    public CategoryAdapter(Context context, ArrayList<String> titlesList, ArrayList<Integer> linksList) {
        mContext = context;
        this.titlesList = titlesList;
        this.linksList = linksList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.categoryitem,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(titlesList.get(position));
        holder.mImageView.setImageResource(linksList.get(position));

    }

    @Override
    public int getItemCount() {
        return titlesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.category_text);
            mImageView=itemView.findViewById(R.id.category_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    switch(position){
                        case 0:{
                            openTab("https://www.cdc.gov/coronavirus/2019-ncov/about/symptoms.html");
                            break;
                        }
                        case 1:{
                            //openTab("https://www.who.int/health-topics/coronavirus#tab=tab_2");
                            openTab("https://www.mohfw.gov.in/Poster_Corona_ad_Eng.pdf");
                            break;
                        }
                        case 2:{
                            openTab("http://124.124.103.93/COVID/home.htm");
                            break;
                        }
                        case 3:{
                            openTab("https://www.worldometers.info/coronavirus/");
                            break;
                        }
                        case 4:{
                            openTab("http://124.124.103.93/COVID/covid/Information/Private%20Hospitals%20with%20Isolation%20Facilities.pdf");
                            break;

                        }
                        case 5:{
                            openTab("https://www.mohfw.gov.in/coronvavirushelplinenumber.pdf");
                            break;
                        }
                        case 6:{
                            openTab("http://124.124.103.93/COVID/covid/Information/corona%20notes.pdf");
                            break;
                        }
                        case 7:{
                            try{
                            String num="+41798931892";
                            String url = "https://api.whatsapp.com/send?phone="+num;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            mContext.startActivity(i);}catch (Exception e){
                                Toast.makeText(mContext, "Please try again later", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }

                }
            });
        }
    }
    public void openTab(String link){
        Uri uri = Uri.parse(link);

// create an intent builder
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

// Begin customizing
// set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));

// set start and exit animations
        intentBuilder.setStartAnimations(mContext, R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(mContext, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

// build custom tabs intent
        CustomTabsIntent customTabsIntent = intentBuilder.build();

// launch the url
        customTabsIntent.launchUrl(mContext, uri);
    }
}

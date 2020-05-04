package com.dayakar.stayhome.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dayakar.stayhome.Data.Case;
import com.dayakar.stayhome.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class StateRecyclerAdapter extends RecyclerView.Adapter<StateRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Case> list;

    public StateRecyclerAdapter(Context context, ArrayList<Case> list) {
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.state_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.state.setText(list.get(position).getState());
        holder.total.setText(list.get(position).getConfirmed());
        holder.active.setText(list.get(position).getActive());
        holder.recovered.setText(list.get(position).getRecovered());
        holder.deceased.setText(list.get(position).getDeaths());
        holder.lastUpadted.setText("Last updated: "+timeStamp(list.get(position).getLastupdatedtime()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView state,total,active,recovered,deceased,lastUpadted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            state=itemView.findViewById(R.id.region_TextView);
            total=itemView.findViewById(R.id.totalcase_value);
            active=itemView.findViewById(R.id.active_Case_value);
            recovered=itemView.findViewById(R.id.recoveredcases_value_textView);
            deceased=itemView.findViewById(R.id.deceasedValuetextView);
            lastUpadted=itemView.findViewById(R.id.lastUpdated_textView);
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

}

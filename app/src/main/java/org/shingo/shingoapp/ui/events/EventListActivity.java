package org.shingo.shingoapp.ui.events;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.R;
import org.shingo.shingoapp.data.GetAsyncData;
import org.shingo.shingoapp.data.OnTaskComplete;
import org.shingo.shingoapp.middle.SEvent.SEvent;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity implements OnTaskComplete {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<SEvent> mEvents;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.event_list);

        mEvents = new ArrayList<>();

        GetAsyncData getEventsAsync = new GetAsyncData(this);
        String[] params = {"/salesforce/events"};
        getEventsAsync.execute(params);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        progress = ProgressDialog.show(this, "", "Loading Events", true);


        mAdapter = new MyAdapter(mEvents);
        mRecyclerView.setAdapter(mAdapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onTaskComplete() {
        throw new UnsupportedOperationException("This method hasn't been implemented...");
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                JSONArray jEvents = result.getJSONArray("events");
                for(int i = 0; i < jEvents.length(); i++){
                    JSONObject jEvent = jEvents.getJSONObject(i);
                    mEvents.add(new SEvent(jEvent.getString("Id"), jEvent.getString("Name"),
                            jEvent.getString("Start_Date__c"), jEvent.getString("End_Date__c")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();
        
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        progress.dismiss();
    }

    private void startActivity(Class dest, Bundle args){
        Intent i = new Intent(this, dest);
        i.putExtras(args);
        startActivity(i);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private List<SEvent> mDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View v){
                super(v);
                mTextView = (TextView) v.findViewById(R.id.info_text);
            }
        }

        public MyAdapter(List<SEvent> data){
            mDataSet = data;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_row, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
            holder.mTextView.setText(mDataSet.get(position).getName());

            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("event_id", mDataSet.get(position).getId());
                    startActivity(EventDetailActivity.class, b);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }
}

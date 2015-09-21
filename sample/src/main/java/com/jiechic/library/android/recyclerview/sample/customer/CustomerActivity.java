package com.jiechic.library.android.recyclerview.sample.customer;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jiechic.library.android.recyclerview.sample.R;
import com.jiechic.library.android.widget.ExtendedRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class CustomerActivity extends ActionBarActivity {
    List<String> myDataset = new ArrayList<>();
    @Bind(R.id.recyclerView)
    ExtendedRecyclerView recyclerView;

    CustomerAdapter adapter = new CustomerAdapter(myDataset);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadmore);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
//        recyclerView.setEmptyView(R.layout.emptyview);
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });


        adapter.setCanLoadMore(true);


        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        myDataset.add("aa");
        adapter.notifyDataSetChanged();
        adapter.setOnLoadListener(() -> recyclerView.postDelayed(() -> {
            onLoadMore();
            adapter.loadComplete();
        }, 2000));

    }

    private void onLoadMore() {


        if (myDataset.size() < 50) {
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            myDataset.add("ccccc");
            adapter.setCanLoadMore(true);
        } else {
            adapter.setCanLoadMore(false);
        }
        adapter.notifyDataSetChanged();
    }

}

package com.Gr8niteout.search;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.config.GPSTracker;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.PubModel;
import com.Gr8niteout.pub.PubActivity;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.Gr8niteout.config.CommonUtilities.isBack;

public class PubRecyclerActivity extends AppCompatActivity {

    PubModel pubModel, model;
    Dialog dialog;
    int pageCount = 1, totalCount;
    int threshold = 0;
    //    View footer;
//    @BindView(R.id.txtempty)
    TextView txtempty;
    //    ProgressBar progressBar;
//    @BindView(R.id.txtTitle)
    TextView txtTitle;
    //    ListView listPubs;
    String latitude = "", longitude = "";
    //    private PubListAdapter adapter;
    Menu menu;
    //    @BindView(R.id.listPubs)

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<PubModel.response.find_pub.find_pubs_lists> list = new ArrayList<>();
    private PubListAdapter mAdapter;
    private OnClickListener mClickListener;
    private OnLoadMoreListener onLoadMoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_list);
        ButterKnife.bind(this);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setIcon(null);

        //set recycler
        list = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        txtempty = findViewById(R.id.txtempty);
        txtTitle = findViewById(R.id.txtTitle);

        mClickListener = new OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(PubRecyclerActivity.this, PubActivity.class);
                intent.putExtra(CommonUtilities.key_pub_id, model.response.find_pub.find_pubs_lists.get(position).pub_id);
                intent.putExtra(CommonUtilities.key_pub_name, model.response.find_pub.find_pubs_lists.get(position).pub_name);
                intent.putExtra(CommonUtilities.key_flag, "true");
                startActivity(intent);
            }
        };

        onLoadMoreListener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (list.size() < totalCount) {
                    if (CommonUtilities.isConnectingToInternet(mContext)) {
                        mAdapter.setLoading(true);
                        pageCount++;
                        list.add(null);
                        mAdapter.notifyItemInserted(list.size() - 1);
                        try {
                            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_feature_id)) {
                                getPubs(pageCount, false, CommonUtilities.key_filter_pub, "filter");
                            } else {
                                getPubs(pageCount, false, CommonUtilities.key_find_pub, "");
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        };

        mAdapter = new PubListAdapter(onLoadMoreListener, mContext, list, mRecyclerView, mClickListener);
        mRecyclerView.setAdapter(mAdapter);
        //

//        footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.progressbar, null, false);
//        progressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
        txtempty.setVisibility(View.GONE);
//        progressBar.setVisibility(View.INVISIBLE);
        CommonUtilities.setFontFamily(PubRecyclerActivity.this, txtempty, CommonUtilities.AvenirLTStd_Medium);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_feature_id)) {
            getPubs(1, true, CommonUtilities.key_filter_pub, "filter");
        } else {
            getPubs(1, true, CommonUtilities.key_find_pub, "");
        }

        if (!getIntent().getExtras().getString("state").equals("") && !getIntent().getExtras().getString("country").equals(""))
            txtTitle.setText(getIntent().getExtras().getString("state") + ", " + getIntent().getExtras().getString("country"));
        else if (getIntent().getExtras().getString("state").equals("") && !getIntent().getExtras().getString("country").equals(""))
            txtTitle.setText(getIntent().getExtras().getString("country"));

//        listPubs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(PubRecyclerActivity.this, PubActivity.class);
//                intent.putExtra(CommonUtilities.key_pub_id, model.response.find_pub.find_pubs_lists.get(position).pub_id);
//                intent.putExtra(CommonUtilities.key_pub_name, model.response.find_pub.find_pubs_lists.get(position).pub_name);
//                intent.putExtra(CommonUtilities.key_flag,"true");
//                startActivity(intent);
//            }
//        });

//        listPubs.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (listPubs.getLastVisiblePosition() >= listPubs.getCount() - 1 - threshold) {
//                    if (totalCount >= pageCount && totalCount!=1 && (progressBar.getVisibility()==View.GONE || progressBar.getVisibility()==View.INVISIBLE)) {
//                        pageCount++;
//                        listPubs.addFooterView(footer);
//                        progressBar.setVisibility(View.VISIBLE);
//                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_feature_id)) {
//                            getPubs(pageCount, false,CommonUtilities.key_filter_pub,"filter");
//                        } else {
//                            getPubs(pageCount, false, CommonUtilities.key_find_pub,"");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
        setFont();
    }

    public void setFont() {
        CommonUtilities.setFontFamily(PubRecyclerActivity.this, txtTitle, CommonUtilities.Avenir);
    }

    public void getPubs(int page, boolean flag, String key, String data) {
        Map<String, String> params = new HashMap<String, String>();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPSTracker gps = new GPSTracker(this);
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
            params.put(CommonUtilities.key_latitude, latitude);
            params.put(CommonUtilities.key_longitude, longitude);
        } else {
            latitude = "";
            longitude = "";
            params.put(CommonUtilities.key_latitude, "");
            params.put(CommonUtilities.key_longitude, "");
        }
        params.put(CommonUtilities.key_country_id, getIntent().getExtras().getString(CommonUtilities.key_country_id));
        params.put(CommonUtilities.key_state_id, getIntent().getExtras().getString(CommonUtilities.key_state_id));
        params.put(CommonUtilities.key_city, getIntent().getExtras().getString(CommonUtilities.key_city));
        params.put(CommonUtilities.key_pub_event_name, getIntent().getExtras().getString(CommonUtilities.key_pub_event_name));
        params.put(CommonUtilities.key_page_no, String.valueOf(page));
        if (data.equals("filter")) {
            params.put(CommonUtilities.key_feature_id, getIntent().getExtras().getString(CommonUtilities.key_feature_id));
            params.put(CommonUtilities.key_sort_by, getIntent().getExtras().getString(CommonUtilities.key_sort_by));
            params.put(CommonUtilities.key_day, getIntent().getExtras().getString(CommonUtilities.key_day));
            params.put(CommonUtilities.key_open_time, getIntent().getExtras().getString(CommonUtilities.key_open_time));
            params.put(CommonUtilities.key_close_time, getIntent().getExtras().getString(CommonUtilities.key_close_time));
        }
        ServerAccess.getResponse(PubRecyclerActivity.this, key, params, flag, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if (mAdapter.isLoading()) {
                    list.remove(list.size() - 1);
                    mAdapter.notifyItemRemoved(list.size());
                }

                pubModel = new PubModel().PubModel(result);
                if (pubModel != null) {
                    if (pubModel.response.status.equals(CommonUtilities.key_Success)) {
                        menu.findItem(R.id.action_filter).setVisible(true);
                        totalCount = (int) pubModel.response.find_pub.count;
                        if (pageCount == 1) {
//                            Double count = Math.ceil(pubModel.response.find_pub.count / pubModel.response.find_pub.limit);
//                            totalCount = count.intValue();
                            model = pubModel;
                            list.addAll(model.response.find_pub.find_pubs_lists);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mAdapter.setLoading(false);
                            mAdapter.notifyDataSetChanged();
//                            listPubs.setVisibility(View.VISIBLE);
//                            adapter = new PubListAdapter();
//                            listPubs.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
//                            listPubs.setEnabled(true);
                            txtempty.setVisibility(View.GONE);
                        } else {
//                            progressBar.setVisibility(View.GONE);
                            list.addAll(pubModel.response.find_pub.find_pubs_lists);
                            model.response.find_pub.find_pubs_lists = list;
                            mAdapter.setLoading(false);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
//                        footer.setVisibility(View.GONE);
                        if (pageCount == 1) {
//                            listPubs.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.GONE);
                            txtempty.setText(pubModel.response.msg);
                            txtempty.setVisibility(View.VISIBLE);
                            menu.findItem(R.id.action_filter).setVisible(false);
                        } else {
                            menu.findItem(R.id.action_filter).setVisible(true);
                            txtempty.setVisibility(View.GONE);
//                            listPubs.removeFooterView(footer);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (mAdapter.isLoading()) {
                    pageCount--;
                    list.remove(list.size() - 1);
                    mAdapter.notifyItemRemoved(list.size());
                }
                mAdapter.setLoading(false);
            }
        });
    }

    /*class PubListAdapter extends BaseAdapter {
        ViewHolder holder = null;

        @Override
        public int getCount() {
            return model.response.find_pub.find_pubs_lists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (PubRecyclerActivity.this).getLayoutInflater();
                view = inflater.inflate(R.layout.pub_list_item,null);
                holder.layoutGray = (RelativeLayout) view.findViewById(R.id.layoutGray);
                holder.imgPub = (ImageView) view.findViewById(R.id.imgPub);
                holder.imgPlaceHolder = (ImageView) view.findViewById(imgPlaceHolder);
                holder.ratingBar1 = (RatingBar) view.findViewById(ratingBar1);
                holder.txtPubName = (TextView) view.findViewById(txtPubName);
                holder.txtPrice = (TextView) view.findViewById(txtPrice);
                holder.txtStatus = (TextView) view.findViewById(txtStatus);
                holder.txtMiles = (TextView) view.findViewById(txtMiles);

                CommonUtilities.setFontFamily(PubRecyclerActivity.this, holder.txtPubName,CommonUtilities.AvenirNextLTPro_Regular);
                CommonUtilities.setFontFamily(PubRecyclerActivity.this, holder.txtPrice,CommonUtilities.AvenirNextLTPro_Regular);
                CommonUtilities.setFontFamily(PubRecyclerActivity.this, holder.txtMiles,CommonUtilities.AvenirNextLTPro_Regular);
                CommonUtilities.setFontFamily(PubRecyclerActivity.this, holder.txtStatus,CommonUtilities.AvenirNextLTPro_Regular);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.txtPubName.setText(model.response.find_pub.find_pubs_lists.get(position).pub_name);

            if(model.response.find_pub.find_pubs_lists.get(position).currency.equals("1"))
                holder.txtPrice.setText("£" + model.response.find_pub.find_pubs_lists.get(position).avg_price);
            else if(model.response.find_pub.find_pubs_lists.get(position).currency.equals("2"))
                holder.txtPrice.setText("€" + model.response.find_pub.find_pubs_lists.get(position).avg_price);
            else if(model.response.find_pub.find_pubs_lists.get(position).currency.equals("3"))
                holder.txtPrice.setText("$" + model.response.find_pub.find_pubs_lists.get(position).avg_price);

            holder.txtPrice.setBackgroundResource(R.drawable.square_edit_text_transfer);

            if (model.response.find_pub.find_pubs_lists.get(position).status.equals("1"))
                holder.txtStatus.setText("Now Open");
            else
                holder.txtStatus.setText("Now Closed");

            if (!model.response.find_pub.find_pubs_lists.get(position).distance.equals("") && !model.response.find_pub.find_pubs_lists.get(position).equals("0"))
                holder.txtMiles.setText(CommonUtilities.doRound(Double.parseDouble(model.response.find_pub.find_pubs_lists.get(position).distance)) + " mi");

//            changed on 28-jan-2019
            Picasso.get().load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_page_URL + model.response.find_pub.find_pubs_lists.get(position).pub_image)
                    .into(holder.imgPub, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.i("", "onsuccess " + position);

                            if (position == 0 && listPubs.getChildAt(0) != null) {
                                listPubs.getChildAt(0).findViewById(R.id.layoutGray).setBackgroundResource(R.mipmap.search_result_transparent);
                                listPubs.getChildAt(0).findViewById(R.id.layoutGray).setAlpha(.5f);
                            }
                            holder.imgPlaceHolder.setVisibility(View.GONE);
                            holder.layoutGray.setBackgroundResource(R.mipmap.search_result_transparent);
                            holder.layoutGray.setAlpha(.5f);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.imgPlaceHolder.setVisibility(View.VISIBLE);
                        }
                    });

            holder.ratingBar1.setRating(Float.parseFloat(model.response.find_pub.find_pubs_lists.get(position).rating));
            holder.ratingBar1.setIsIndicator(true);
            return view;
        }

        public class ViewHolder {

            private RelativeLayout layoutGray;
            private ImageView imgPub;
            private ImageView imgPlaceHolder;
            private RatingBar ratingBar1;
            private TextView txtPubName;
            private TextView txtPrice;
            private TextView txtStatus;
            private TextView txtMiles;
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_filter) {
            if (txtempty.getVisibility() != View.VISIBLE) {
                Intent intent = new Intent(PubRecyclerActivity.this, FilterActivity.class);
                intent.putExtra(CommonUtilities.key_country_id, getIntent().getExtras().getString(CommonUtilities.key_country_id));
                intent.putExtra(CommonUtilities.key_state_id, getIntent().getExtras().getString(CommonUtilities.key_state_id));
                intent.putExtra(CommonUtilities.key_city, getIntent().getExtras().getString(CommonUtilities.key_city));
                intent.putExtra(CommonUtilities.key_pub_event_name, getIntent().getExtras().getString(CommonUtilities.key_pub_event_name));
                intent.putExtra(CommonUtilities.key_latitude, latitude);
                intent.putExtra(CommonUtilities.key_longitude, longitude);
                intent.putExtra("country", getIntent().getExtras().getString("country"));
                intent.putExtra("state", getIntent().getExtras().getString("state"));

                if (getIntent().getExtras().containsKey(CommonUtilities.key_feature_id)) {
                    super.onBackPressed();
                } else {
                    startActivity(intent);
                }
            }
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            case R.id.action_filter:
//                if (txtempty.getVisibility() != View.VISIBLE) {
//                    Intent intent = new Intent(PubRecyclerActivity.this, FilterActivity.class);
//                    intent.putExtra(CommonUtilities.key_country_id, getIntent().getExtras().getString(CommonUtilities.key_country_id));
//                    intent.putExtra(CommonUtilities.key_state_id, getIntent().getExtras().getString(CommonUtilities.key_state_id));
//                    intent.putExtra(CommonUtilities.key_city, getIntent().getExtras().getString(CommonUtilities.key_city));
//                    intent.putExtra(CommonUtilities.key_pub_event_name, getIntent().getExtras().getString(CommonUtilities.key_pub_event_name));
//                    intent.putExtra(CommonUtilities.key_latitude, latitude);
//                    intent.putExtra(CommonUtilities.key_longitude, longitude);
//                    intent.putExtra("country", getIntent().getExtras().getString("country"));
//                    intent.putExtra("state", getIntent().getExtras().getString("state"));
//                    if (getIntent().getExtras().containsKey(CommonUtilities.key_feature_id)) {
//                        super.onBackPressed();
//                    } else {
//                        startActivity(intent);
//                    }
//                }
//                return true;
//        }
//        return false;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBack = true;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtilities.isBack) {
            CommonUtilities.isBack = false;
            super.onBackPressed();

        }
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnClickListener {
        void onClick(int position);
    }
}



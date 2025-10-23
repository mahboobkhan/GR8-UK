package com.Gr8niteout.ActivityFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.DrinkModel;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.model.UserLoginResponse;
import com.Gr8niteout.signup.SignupLogin;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

import static com.Gr8niteout.R.id.recycler_view;


public class Drinks extends Fragment {

    MainActivity mActivity;
    DrinkModel model;
    DrinkModel modelDrink;
    SignUpModel modelSignup;
    UserLoginResponse userLoginModel;
    SwipeRefreshLayout swipeRefreshLayout;
    int pageCount = 1, totalCount, threshold = 0;
    ArrayList<DrinkModel.response.drinklist.drinks_lists> list = new ArrayList<>();
    String userid;
    RecyclerView listDrinks;
    View footer;
    DrinkListAdapter1 adapter;
    //  ProgressBar progressBar;
    FrameLayout listviewLayout;
    LinearLayout notLoginLayout;
    ImageView drinkImage;
    TextView textStatic, textStatic_login;
    ImageView btnLogin;
    LinearLayoutManager layoutManager;
    OnEventListener listner;
    boolean isLoading = false;

    int totalDrinkCount;
//    @BindView(R.id.lin_dialog)
    LinearLayout lin_dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_drinks, container, false);
        ButterKnife.bind(this, v);
        listviewLayout = (FrameLayout) v.findViewById(R.id.listview_layout);
        notLoginLayout = (LinearLayout) v.findViewById(R.id.not_login_layout);
        drinkImage = (ImageView) v.findViewById(R.id.drink_image);
        textStatic = (TextView) v.findViewById(R.id.textStatic);
        textStatic_login = (TextView) v.findViewById(R.id.textStatic_login);
        lin_dialog =  v.findViewById(R.id.lin_dialog);
        CommonUtilities.setFontFamily(mActivity, textStatic_login, CommonUtilities.AvenirLTStd_Medium);
        btnLogin = (ImageView) v.findViewById(R.id.btn_login);
        // Safely parse user data with null checks
        String userDataString = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData);
        if (userDataString != null && !userDataString.isEmpty()) {
            try {
                // Try to parse as SignUpModel first (for Facebook login)
                modelSignup = new SignUpModel().SignUpModel(userDataString);
                if (modelSignup == null) {
                    // If SignUpModel parsing fails, try UserLoginResponse (for email/password login)
                    Gson gson = new Gson();
                    userLoginModel = gson.fromJson(userDataString, UserLoginResponse.class);
                }
            } catch (Exception e) {
                // Handle parsing errors gracefully
                modelSignup = null;
                userLoginModel = null;
            }
        } else {
            modelSignup = null;
            userLoginModel = null;
        }
        setHasOptionsMenu(true);
        listDrinks = (RecyclerView) v.findViewById(recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        setFont();

        /*if (!CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            listviewLayout.setVisibility(View.VISIBLE);
            notLoginLayout.setVisibility(View.GONE);
            userid = modelSignup.response.user_data.user_id;
            getDrinkList(pageCount, true);
        } else if (CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            listviewLayout.setVisibility(View.GONE);
            notLoginLayout.setVisibility(View.VISIBLE);
            textStatic_login.setVisibility(View.VISIBLE);
            textStatic.setVisibility(View.GONE);
        }*/

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, SignupLogin.class);
                i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_drinks);
                startActivity(i);
            }
        });

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listDrinks.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("", "RS swipeRefreshLayout");

                pageCount = 1;
                list.clear();
                adapter.notifyDataSetChanged();
                getDrinkList(pageCount, false);
                swipeRefreshLayout.setRefreshing(true);

            }
        });
        lin_dialog.setVisibility(View.GONE);
        listDrinks.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int total = layoutManager.getItemCount();
                int firstVisibleItemCount = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemCount = layoutManager.findLastVisibleItemPosition();


                if (!isLoading) {
                    if (total > 0)
                        if ((total - 1) == lastVisibleItemCount) {
                            if (list.size() < totalDrinkCount) {
                                pageCount++;
                                lin_dialog.setVisibility(View.VISIBLE);
                                isLoading = true;
                                getDrinkList(pageCount, false);
                            } else {
                                lin_dialog.setVisibility(View.GONE);
                            }
                        } else {
                            lin_dialog.setVisibility(View.GONE);
                        }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        listner = new OnEventListener() {
            @Override
            public void ClickEvent(View v) {
                Gson gson = new Gson();
                String detail = gson.toJson(list.get(listDrinks.getChildLayoutPosition(v)));
                CommonUtilities.setPreference(getActivity(), CommonUtilities.pref_drinks_detail, detail);
                Intent intent = new Intent(mActivity, DrinkDetails.class);
                startActivity(intent);
            }
        };

        CommonUtilities.setFontFamily(mActivity, textStatic, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, textStatic_login, CommonUtilities.AvenirLTStd_Medium);
        setHasOptionsMenu(true);
        return v;
    }

    public interface OnEventListener {
        void ClickEvent(View v);
    }

    public void getDrinkList(final int pageCountt, boolean loader) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId));
        
        // Safely get email from user data
        String email = "";
        if (modelSignup != null && modelSignup.response != null && modelSignup.response.user_data != null) {
            email = modelSignup.response.user_data.email != null ? modelSignup.response.user_data.email : "";
        } else if (userLoginModel != null && userLoginModel.response != null && userLoginModel.response.responseInfo != null && userLoginModel.response.responseInfo.data != null) {
            email = userLoginModel.response.responseInfo.data.end_email != null ? userLoginModel.response.responseInfo.data.end_email : "";
        }
        params.put(CommonUtilities.key_email, email);
//        params.put(CommonUtilities.key_mobile, modelSignup.response.user_data.mobile);
        params.put(CommonUtilities.key_page_no, String.valueOf(pageCountt));

        ServerAccess.getResponse(mActivity, CommonUtilities.key_get_drink_list, params, loader, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                lin_dialog.setVisibility(View.GONE);

                Log.d("key_get_drink_list", "onSuccess: "+result);

                modelDrink = new DrinkModel().DrinkModel(result);
                if (modelDrink != null) {
                    if (modelDrink.response.status.equals(CommonUtilities.key_Success)) {
                        if (modelDrink.response != null) {
                            isLoading = false;
                            totalDrinkCount = Integer.valueOf(modelDrink.response.drinklist.count);
                            if (pageCountt == 1) {
                                Double count = Math.ceil(Double.parseDouble(modelDrink.response.drinklist.count) / Double.parseDouble(String.valueOf(modelDrink.response.drinklist.limit)));
                                totalCount = count.intValue();
                                model = modelDrink;
                                list.addAll(model.response.drinklist.drinks_lists);
                                adapter = new DrinkListAdapter1(list, listner);
                                listDrinks.setAdapter(adapter);
                                swipeRefreshLayout.setRefreshing(false);
                                //    listDrinks.setEnabled(true);

                            } else {
                                list.addAll(modelDrink.response.drinklist.drinks_lists);
                                model.response.drinklist.setDrinks_lists(list);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        if (pageCountt == 1) {
                            //    listDrinks.setAdapter(null);
                            swipeRefreshLayout.setRefreshing(false);
                            listviewLayout.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.GONE);
                            notLoginLayout.setVisibility(View.VISIBLE);
                            textStatic_login.setVisibility(View.GONE);
                            textStatic.setVisibility(View.VISIBLE);

                        } else {
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }


    public class DrinkListAdapter1 extends RecyclerView.Adapter<DrinkListAdapter1.MyViewHolder> {

        public ArrayList<DrinkModel.response.drinklist.drinks_lists> model;
        OnEventListener listner;

        public DrinkListAdapter1(ArrayList<DrinkModel.response.drinklist.drinks_lists> items, OnEventListener lis) {
            this.model = items;
            this.listner = lis;

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txtName;
            TextView txtStatic;
            TextView days_ago;
            ImageView imageView;
            LinearLayout listcolor;
            RelativeLayout image_layout;
            TextView First_Lett;


            public MyViewHolder(View convertView) {
                super(convertView);
                txtName = (TextView) convertView.findViewById(R.id.txtName);
                txtStatic = (TextView) convertView.findViewById(R.id.txtStatic);
                days_ago = (TextView) convertView.findViewById(R.id.days_ago);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                listcolor = (LinearLayout) convertView.findViewById(R.id.listcolor);
                image_layout = (RelativeLayout) convertView.findViewById(R.id.image_layout);
                First_Lett = (TextView) convertView.findViewById(R.id.First_Lett);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drink_list_row, parent, false);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.ClickEvent(view);
                }
            });
            return new MyViewHolder(itemView);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.txtName.setText(this.model.get(position).names);
            holder.days_ago.setText(this.model.get(position).days_ago);

            if (position % 2 == 0) {
                holder.listcolor.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.listodd));
            } else {
                holder.listcolor.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            }

            holder.First_Lett.setVisibility(View.GONE);

            if (this.model.get(position).photo.equals("")) {
                holder.imageView.setImageResource(R.mipmap.no_user);
                if (!this.model.get(position).names.equals("")) {
                    holder.First_Lett.setVisibility(View.VISIBLE);
                    holder.First_Lett.setText(model.get(position).names.substring(0, 1).toUpperCase());
                } else {
                    holder.imageView.setImageResource(R.mipmap.user);
                    holder.First_Lett.setVisibility(View.GONE);
                }
            } else {
//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL +
                                this.model.get(position).photo)
                        .error(R.mipmap.user).placeholder(R.mipmap.user)
                        .transform(new CircleTransform())
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }

            CommonUtilities.setFontFamily(mActivity, holder.txtName, CommonUtilities.AvenirNextLTPro_Demi);
            CommonUtilities.setFontFamily(mActivity, holder.txtStatic, CommonUtilities.AvenirLTStd_Medium);
            CommonUtilities.setFontFamily(mActivity, holder.days_ago, CommonUtilities.AvenirLTStd_Medium);

        }

        @Override
        public int getItemCount() {
            return this.model.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            listviewLayout.setVisibility(View.VISIBLE);
            notLoginLayout.setVisibility(View.GONE);
            
            // Safely get user ID from user data
            String userId = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
            if (userId.equals("")) {
                // Try to extract from user data if pref_UserId is empty
                if (modelSignup != null && modelSignup.response != null && modelSignup.response.user_data != null) {
                    userId = modelSignup.response.user_data.user_id != null ? modelSignup.response.user_data.user_id : "";
                } else if (userLoginModel != null && userLoginModel.response != null && userLoginModel.response.responseInfo != null && userLoginModel.response.responseInfo.data != null) {
                    userId = userLoginModel.response.responseInfo.data.user_id != null ? userLoginModel.response.responseInfo.data.user_id : "";
                }
            }
            
            if (!userId.equals("")) {
                userid = userId;
                pageCount = 1;
                list.clear();
                getDrinkList(pageCount, true);
            } else {
                // If no user ID available, show login layout
                listviewLayout.setVisibility(View.GONE);
                notLoginLayout.setVisibility(View.VISIBLE);
            }
        } else if (CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            listviewLayout.setVisibility(View.GONE);
            notLoginLayout.setVisibility(View.VISIBLE);
            textStatic_login.setVisibility(View.VISIBLE);
            textStatic.setVisibility(View.GONE);
        }
        /*pageCount = 1;
        list.clear();
        getDrinkList(pageCount, true);*/

    }

    public void setFont() {
        CommonUtilities.setFontFamily(mActivity, textStatic, CommonUtilities.AvenirLTStd_Medium);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        menu.findItem(R.id.action_logout).setVisible(true);
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(true);
        menu.findItem(R.id.action_transaction_history).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search_white) {
            mActivity.selectItem(1);
            mActivity.mDrawerList.setSelection(1);
            mActivity.mDrawerList.setItemChecked(1, true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication) mActivity.getApplication()).getDefaultTracker().setScreenName("Drinks Listing Screen");
        ((MyApplication) mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}

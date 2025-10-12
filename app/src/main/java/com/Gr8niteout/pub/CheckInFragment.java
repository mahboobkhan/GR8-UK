package com.Gr8niteout.pub;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.adapter.CheckInAdapter;
import com.Gr8niteout.buycredits.BuyCreditActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.home.HomeFragment;
import com.Gr8niteout.model.CheckedInUserModel;
import com.Gr8niteout.model.CheckinResponse;
import com.Gr8niteout.model.PubProfile;
import com.Gr8niteout.signup.SignupLogin;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckInFragment extends Fragment {

    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private LinearLayout llCheckin;
    private LinearLayout noLoginLayout;
    private TextView txtNoLogin;
    private TextView btnCheckIn;

    private String userid = "";
    private String pub_id;
    private int pageCount = 0;

    private List<CheckedInUserModel.CheckInData> mList = new ArrayList<>();
    private CheckInAdapter mCheckInAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private OnLoadMoreListener onLoadMoreListener;
    private int totalCount = 0;
    private OnClickCancelListener mClickListener;
    private int pageLimit = 5;
    private PubProfile.Response.Pub_Profile pubProfileModel;

    public CheckInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_checkin);
        mSwipeRefresh = view.findViewById(R.id.swiperefresh_checkin);
//        llCheckin = view.findViewById(R.id.img_checkin);
        noLoginLayout = view.findViewById(R.id.not_login_layout);
        txtNoLogin = view.findViewById(R.id.not_login_txt);
        btnCheckIn = view.findViewById(R.id.btn_check_in);

        CommonUtilities.setFontFamily(mActivity, txtNoLogin, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, btnCheckIn, CommonUtilities.Avenir_Heavy);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

        userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);//
        if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_pub_id)) {
            pub_id = getArguments().getString(CommonUtilities.key_pub_id);
        }

        /*llCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userid.equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                    builder1.setTitle(R.string.app_name);
                    builder1.setIcon(R.mipmap.app_icon);
                    builder1.setMessage("Please login to check-in");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent i = new Intent(mActivity, SignupLogin.class);
                            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
                            startActivityForResult(i, 111);
                        }
                    });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    //call check-in service
                    callCheckIn();
                }
            }
        });*/

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userid.equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                    builder1.setTitle(R.string.app_name);
                    builder1.setIcon(R.mipmap.app_icon);
                    builder1.setMessage("Please login to check-in");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent i = new Intent(mActivity, SignupLogin.class);
                            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
                            startActivityForResult(i, 111);
                        }
                    });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    //call check-in service
                    callCheckIn();
                }
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (CommonUtilities.isConnectingToInternet(mActivity)) {
                if (!mCheckInAdapter.isLoading()) {
                    mCheckInAdapter.setLoading(false);
                    mList.clear();
                    mCheckInAdapter.notifyDataSetChanged();
                    try {
                        getCheckedInUser(false);
                    } catch (Exception e) {
                        Log.e("Checkin", e.getMessage());
                    }
                }
//                } else
//                    mSwipeRefresh.setRefreshing(false);
            }
        });

        onLoadMoreListener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mList.size() < totalCount && !mSwipeRefresh.isRefreshing()) {
                    if (CommonUtilities.isConnectingToInternet(mActivity)) {
                        mCheckInAdapter.setLoading(true);
                        pageCount++;
                        mList.add(null);
                        mCheckInAdapter.notifyItemInserted(mList.size() - 1);
                        try {
                            getCheckedInUser(false);
                        } catch (Exception e) {
                            Log.e("Checkin", e.getMessage());
                        }
                    }
                }
            }
        };


        pubProfileModel = new Gson().fromJson(CommonUtilities.getPreference(mActivity, CommonUtilities.pref_pub_detail), PubProfile.Response.Pub_Profile.class);

        mClickListener = new OnClickCancelListener() {
            @Override
            public void onClick(int position) {
                if (!TextUtils.isEmpty(pubProfileModel.pub_type) && pubProfileModel.pub_type.equals("1")) {
                    CommonUtilities.setPreference(mActivity, CommonUtilities.key_checkin_data, new Gson().toJson(mList.get(position)));
                    Intent i = new Intent(mActivity, BuyCreditActivity.class);
                    i.putExtra(CommonUtilities.key_pub_name, pubProfileModel.pub_name);
                    i.putExtra(CommonUtilities.key_pub_image, pubProfileModel.banner_array.get(0));
                    i.putExtra(CommonUtilities.key_pub_id, pubProfileModel.pub_id);
                    i.putExtra("currency", pubProfileModel.currency);
                    i.putExtra("from", "CheckIn");
                    i.putExtra("recipient_name", mList.get(position).first_name + " " + mList.get(position).last_name);
                    i.putExtra("recipient_email", mList.get(position).email);
                    startActivity(i);
                }

            }
        };

        mCheckInAdapter = new CheckInAdapter(mActivity, mList, mClickListener, onLoadMoreListener, mRecyclerView,pubProfileModel.pub_type);
        mRecyclerView.setAdapter(mCheckInAdapter);


        try {
            getCheckedInUser(true);
        } catch (Exception e) {
            CommonUtilities.alertdialog(mActivity, "Something went wrong. Please try again");
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mCheckInAdapter != null) {
                mCheckInAdapter.setLoading(false);
                mList.clear();
                try {
                    getCheckedInUser(true);
                } catch (Exception e) {
                    CommonUtilities.alertdialog(mActivity, "Something went wrong. Please try again");
                }
            }
        }
    }

    private void getCheckedInUser(boolean showDialog) throws Exception {
        Map<String, String> param = new HashMap<>();

        if (mCheckInAdapter.isLoading())
            pageCount = (mList.size() - 1) / pageLimit + 1;
        else
            pageCount = (mList.size()) / pageLimit + 1;

        param.put(CommonUtilities.key_page_no, String.valueOf(pageCount));
        param.put(CommonUtilities.key_pub_id, pub_id);
        param.put(CommonUtilities.key_user_id, userid);

        ServerAccess.getResponse(mActivity, CommonUtilities.key_get_checked_in_user, param, showDialog, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                CheckedInUserModel model = CheckedInUserModel.getModel(result);
                if (mCheckInAdapter.isLoading()) {
                    mList.remove(mList.size() - 1);
                    mCheckInAdapter.notifyItemRemoved(mList.size());
                    mCheckInAdapter.setLoading(false);
                }
                if (CommonUtilities.key_Success.equalsIgnoreCase(model.response.status)
                        &&
                        model.response.checkin_user.check_in_data != null) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    noLoginLayout.setVisibility(View.GONE);

                    if (model.response.checkin_user.limit != 0)
                        pageLimit = model.response.checkin_user.limit;
                    totalCount = Integer.parseInt(model.response.checkin_user.count);
                    if (pageCount == 1)
                        mList.clear();
                    mList.addAll(model.response.checkin_user.check_in_data);

                    mCheckInAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    noLoginLayout.setVisibility(View.VISIBLE);
//                    CommonUtilities.ShowToast(mActivity, model.response.checkin_user.error);
                }
                mSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                if (mCheckInAdapter.isLoading()) {
                    pageCount--;
                    mList.remove(mList.size() - 1);
                    mCheckInAdapter.notifyItemRemoved(mList.size());
                }
                mSwipeRefresh.setRefreshing(false);
                mCheckInAdapter.setLoading(false);
            }
        });

    }

    private void callCheckIn() {
        Map<String, String> params = new HashMap<>();
        params.put(CommonUtilities.key_pub_id, pub_id);
        params.put(CommonUtilities.key_user_id, userid);

        ServerAccess.getResponse(mActivity, CommonUtilities.key_chcek_in, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                {"response":{"status":"Success","code":"SUC001","checkin_user":{"success":"You are checked in successfully."}}}
                CheckinResponse mResponse = new CheckinResponse().getModel(result);

                if (mResponse != null) {
                    if (CommonUtilities.key_Success.equals(mResponse.response.status)) {
                        CommonUtilities.ShowToast(mActivity, mResponse.response.checkin_user.success);
                        try {
                            getCheckedInUser(true);
                        } catch (Exception e) {
//                            Log.e("Checkin", e.getMessage());
                        }
                    } else {
                        CommonUtilities.alertdialog(mActivity, mResponse.response.msg);
                    }
                } else {
                    CommonUtilities.alertdialog(mActivity, "Something went wrong. Please try again");
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    //chekin feature
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_flag)) {
                MainActivity.act.setDrawerItem();
                MainActivity.act.mDrawerList.setItemChecked(1, true);
            } else {
                MainActivity.act.setDrawerItem();
                MainActivity.act.mDrawerList.setItemChecked(0, true);
                userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
                FragmentManager fm = ((FragmentActivity) MainActivity.act).getSupportFragmentManager();
                HomeFragment currentFragment = (HomeFragment) fm.findFragmentByTag("Home");
                currentFragment.setDetails();
            }

        }
    }


    public interface OnClickCancelListener {
        void onClick(int position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}

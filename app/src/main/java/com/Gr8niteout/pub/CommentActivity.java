package com.Gr8niteout.pub;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.RoundImageview;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.home.HomeFragment;
import com.Gr8niteout.model.CommentModel;
import com.Gr8niteout.model.Post_CommentModel;
import com.Gr8niteout.signup.SignupLogin;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import at.blogc.android.views.ExpandableTextView;

public class CommentActivity extends AppCompatActivity {


    int position;
    public static RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecycleAdapter recyclerViewAdapter;
    CommentModel model2;
    Post_CommentModel model3;
    String pub_id, img_id, user_id = "";
    Toolbar toolbar;
    EditText edit_comment;
    TextView tool_text;
    TextView textNocommnt;
    ImageView no_comment_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        edit_comment = (EditText) findViewById(R.id.edit_comment);
        edit_comment.setCursorVisible(true);
        tool_text = (TextView) findViewById(R.id.tool_text);
        textNocommnt = (TextView) findViewById(R.id.textNocommnt);
        no_comment_img = (ImageView) findViewById(R.id.no_comment_img);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(10);
        CommonUtilities.showSoftKeyboard(CommentActivity.this, edit_comment);
        Intent get = getIntent();
        if (get != null) {
            pub_id = get.getStringExtra("pub_id");
            img_id = get.getStringExtra("img_id");
        }
        getSupportActionBar().setTitle(null);
        CommonUtilities.setFontFamily(CommentActivity.this, tool_text, CommonUtilities.AvenirLTStd_Medium);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutManager = new LinearLayoutManager(CommentActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecycleAdapter();
        CommonUtilities.setFontFamily(CommentActivity.this, edit_comment, CommonUtilities.AvenirLTStd_Medium);

    }

    public void PostClick(View v) {

        Log.d("user_id",user_id);
        if (user_id.equals("")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(CommentActivity.this);
            builder1.setTitle(R.string.app_name);
            builder1.setIcon(R.mipmap.app_icon);
            builder1.setMessage("Please login to post a comment");
            builder1.setCancelable(true);
            builder1.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Intent i = new Intent(CommentActivity.this, SignupLogin.class);
                    i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
                    startActivity(i);
                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            Button bq = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
            bq.setTextColor(Color.parseColor("#0053A8"));
        } else {
            if (edit_comment.getText().toString().equals("")) {
                Toast.makeText(CommentActivity.this, "There is nothing to post", Toast.LENGTH_SHORT).show();
            } else {
                postComments();
            }
        }
    }

//    public void CommentClick(View v) {
//
//        if (user_id.equals("")) {
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(CommentActivity.this);
//            builder1.setTitle(R.string.app_name);
//            builder1.setIcon(R.mipmap.app_icon);
//            builder1.setMessage("Please login to post a comment");
//            builder1.setCancelable(true);
//            builder1.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.cancel();
//                    Intent i = new Intent(CommentActivity.this, SignupLogin.class);
//                    i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
//                    startActivityForResult(i, 110);
//                }
//            });
//
//            AlertDialog alert11 = builder1.create();
//            alert11.show();
//            Button bq = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
//            bq.setTextColor(Color.parseColor("#0053A8"));
//        }
////        else {
////            edit_comment.setEnabled(true);
////            edit_comment.setFocusable(true);
////            edit_comment.setFocusableInTouchMode(true);
////        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("", "RS " + img_id + "   " + pub_id + " requestCode " + requestCode);
        if (requestCode == 110 && resultCode == RESULT_OK) {
            MainActivity.act.setDrawerItem();
//            MainActivity.act.adapter.notifyDataSetChanged();
            MainActivity.act.mDrawerList.setItemChecked(0, true);
            MainActivity.act.mDrawerList.setSelection(0);
            user_id = CommonUtilities.getPreference(CommentActivity.this, CommonUtilities.pref_UserId);
//            FragmentManager fm = getSupportFragmentManager();
//            HomeFragment fragment = (HomeFragment)fm.findFragmentById(R.id.container_body);
//            fragment.setDetails();
            FragmentManager fm = ((FragmentActivity) MainActivity.act).getSupportFragmentManager();
            HomeFragment currentFragment = (HomeFragment) fm.findFragmentByTag("Home");
//            if (currentFragment!=null && currentFragment.isVisible()){
            currentFragment.setDetails();
//            }

        }
    }

    public void getComments() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_pub_id, pub_id);
        params.put(CommonUtilities.key_img_id, img_id);
//        params.put(CommonUtilities.key_pub_id,"306");
        Log.d("params",params.toString());
        ServerAccess.getResponse(CommentActivity.this, CommonUtilities.key_get_comments, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model2 = new CommentModel().CommentModel(result);
                if (model2 != null) {
                    if (model2.response.status.equals(CommonUtilities.key_Success)) {
                        CommonUtilities.setPreference(CommentActivity.this, CommonUtilities.pref_Comments, result);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        if (!CommonUtilities.getPreference(CommentActivity.this, CommonUtilities.pref_UserId).equals("")) {
                            MainActivity.act.setDrawerItem();
                            MainActivity.act.mDrawerList.setItemChecked(0, true);
                            MainActivity.act.mDrawerList.setSelection(0);
                        }
                    } else {
                        textNocommnt.setText(model2.response.msg);
                        textNocommnt.setVisibility(View.VISIBLE);
                        no_comment_img.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void postComments() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_user_id, user_id);
        params.put(CommonUtilities.key_pub_id, pub_id);
        params.put(CommonUtilities.key_img_id, img_id);
        params.put(CommonUtilities.key_comment, edit_comment.getText().toString());

//        params.put(CommonUtilities.key_pub_id,"306");
        ServerAccess.getResponse(CommentActivity.this, CommonUtilities.key_post_comment, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model3 = new Post_CommentModel().Post_CommentModel(result);
                if (model3 != null) {
                    if (model3.response.status.equals(CommonUtilities.key_Success)) {
//                        Toast.makeText(CommentActivity.this,model3.response.status,Toast.LENGTH_SHORT).show();
                        edit_comment.setText("");
                        Intent i = new Intent(CommentActivity.this, PostCommentSuccessfulAct.class);
                        startActivity(i);
                    } else {
                        CommonUtilities.ShowToast(CommentActivity.this, model3.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment_model, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.userName.setText(model2.response.comments_list.get(position).user_name);
            holder.expandableTextView.setText(model2.response.comments_list.get(position).comment);
            holder.daysAgo.setText(model2.response.comments_list.get(position).days_ago);

            if (holder.expandableTextView.getText().toString().length() >= 200) {
                holder.buttonToggle.setVisibility(View.VISIBLE);
            }
            holder.expandableTextView.setAnimationDuration(1000L);

            holder.expandableTextView.setInterpolator(new OvershootInterpolator());


            holder.expandableTextView.setExpandInterpolator(new OvershootInterpolator());
            holder.expandableTextView.setCollapseInterpolator(new OvershootInterpolator());

            holder.buttonToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (holder.expandableTextView.isExpanded()) {
                        holder.expandableTextView.collapse();
                        holder.buttonToggle.setText("view more");
                    } else {
                        holder.expandableTextView.expand();
                        holder.buttonToggle.setText("view less");
                    }
                }
            });


            if (model2.response.comments_list.get(position).user_image.equals("")) {
                if (!model2.response.comments_list.get(position).user_name.equals("")) {
                    holder.First_Lett.setText(model2.response.comments_list.get(position).user_name.substring(0, 1));
                    holder.image_layout.setBackgroundResource(R.drawable.round);
                } else {
                    holder.image_layout.setBackgroundResource(R.mipmap.user);
                }
            } else {
//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL +
                                model2.response.comments_list.get(position).user_image)
//                    "")
                        .error(R.mipmap.user)
                        .placeholder(R.mipmap.user) // optional
                        .into(holder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }

                        });
            }
            CommonUtilities.setFontFamily(CommentActivity.this, holder.userName, CommonUtilities.AvenirLTStd_Medium);
            CommonUtilities.setFontFamily(CommentActivity.this, holder.daysAgo, CommonUtilities.AvenirLTStd_Medium);
            CommonUtilities.setFontFamily(CommentActivity.this, holder.expandableTextView, CommonUtilities.AvenirLTStd_Medium);

        }

        @Override
        public int getItemCount() {
            return model2.response.comments_list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            RoundImageview userImage;
            TextView userName;
            TextView daysAgo;
            final ExpandableTextView expandableTextView;
            TextView First_Lett;
            TextView buttonToggle;
            RelativeLayout image_layout;

            public ViewHolder(View view) {
                super(view);
                userImage = (RoundImageview) view.findViewById(R.id.user_image);
                userName = (TextView) view.findViewById(R.id.user_name);
                daysAgo = (TextView) view.findViewById(R.id.days_ago);
                image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
                expandableTextView = (ExpandableTextView) view.findViewById(R.id.txtCommnt);
                buttonToggle = (TextView) view.findViewById(R.id.button_toggle);
                First_Lett = (TextView) view.findViewById(R.id.First_Lett);
            }

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        user_id = CommonUtilities.getPreference(CommentActivity.this, CommonUtilities.pref_UserId);
        getComments();
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
}

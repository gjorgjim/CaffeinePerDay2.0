package com.gjorgjimarkov.caffeineperday;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DrinksListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MyExpendableListAdapter listAdapter;
    private ExpandableListView list;
    private ArrayList<ParentDrink> parentList = new ArrayList<>();
    private EditText search;
    private RelativeLayout searchLayout;
    private int caffeine=0;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    private TextView calculate;
    private int dosemax=400;
    private List<Drinks> chosedDrinks = new ArrayList<>();
    private ImageView viewList;
    private AdView ad;
    private RelativeLayout footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks_list);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            dosemax = bundle.getInt("dosemax");
        }

//        boolean connected = false;
//        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            //we are connected to a network
//            connected = true;
//        }
//        else {
//            connected = false;
//        }
//
//        footer = (RelativeLayout)findViewById(R.id.footerLayout);
//
//        ad = (AdView)findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        ad.loadAd(adRequest);
//
//        ViewGroup.LayoutParams lp = ad.getLayoutParams();
//        final ViewGroup.LayoutParams lpBefore = footer.getLayoutParams();
//
//        ad.setVisibility(AdView.GONE);
//        footer.setLayoutParams(lp);
//
//        if(connected) {
//            Animation footerIn = new Animation() {
//                @Override
//                public void startNow() {
//                    footer.setLayoutParams(lpBefore);
//                }
//
//                @Override
//                public void setStartOffset(long startOffset) {
//                    super.setStartOffset(3000);
//                }
//            };
//            footerIn.setDuration(200);
//            Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
//            fadeIn.setStartOffset(3000);
//            fadeIn.setDuration(200);
//            footer.startAnimation(footerIn);
//            ad.startAnimation(fadeIn);
//            ad.setVisibility(AdView.VISIBLE);
//        }


        calculate = (TextView)findViewById(R.id.calculate);

        viewList = (ImageView)findViewById(R.id.viewList);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");

        parentList = DataDrinks.getList();

        list = (ExpandableListView) findViewById(R.id.expandableListView);
        listAdapter = new MyExpendableListAdapter(DrinksListActivity.this, parentList);

        list.setAdapter(listAdapter);

        search = (EditText)findViewById(R.id.searchEdit);

        searchLayout = (RelativeLayout)findViewById(R.id.searchLayout);
        searchLayout.setVisibility(RelativeLayout.INVISIBLE);

        if(search.getVisibility() == EditText.VISIBLE) {
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    listAdapter.filterData(s.toString());
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!listAdapter.filterData(s.toString())) {
                        Toast.makeText(getApplicationContext(), "Drink not found",
                                Toast.LENGTH_SHORT).show();
                    }
                    listAdapter.notifyDataSetChanged();
                }
            });
        }

        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                parentList.get(groupPosition).getArray().get(childPosition).incraseNumber();
                listAdapter.notifyDataSetChanged();
                caffeine+=parentList.get(groupPosition).getArray().get(childPosition).getCaff();
                boolean alredyAdded = false;
                for(int i = 0; i<chosedDrinks.size(); i++) {
                    if(chosedDrinks.get(i).getName().equals(parentList.get(groupPosition).getArray().get(childPosition).getName())) {
                        chosedDrinks.get(i).incraseNumber();
                        alredyAdded = true;
                        break;
                    }
                }
                if(!alredyAdded) {
                    chosedDrinks.add(parentList.get(groupPosition).getArray().get(childPosition));
                }
                return true;
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(DrinksListActivity.this);
                alert.setIcon(R.drawable.alert_icon);
                alert.setTitle("Caffeine per day");
                if (caffeine < dosemax - (dosemax / 3)) {
                    alert.setMessage("Your caffeine for today is: " + caffeine + " mg.\n" +
                            "You can have a coffee or two more today.\n" +
                            "For you, up to " + dosemax + "mg caffeine can be consumed per day without any adverse effects.");
                } else {
                    if (caffeine > dosemax) {
                        alert.setMessage("Your caffeine for today is: " + caffeine + "mg.\n" +
                                "That is too much caffeine for you today.\n" +
                                "For you, up to " + dosemax + "mg caffeine can be consumed per day without any adverse effects.");
                    } else {
                        alert.setMessage("Your caffeine for today is: " + caffeine + "mg.\n" +
                                "You have had enough caffeine today.\n" +
                                "For you, up to " + dosemax + "mg caffeine can be consumed per day without any adverse effects.");
                    }

                }
                alert.setNegativeButton("Close the app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alert.setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        });
                    }
                });
                alert.show();
                resetArray();
            }
        });

        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosedDrinks.size() != 0) {
                    ChosedDrinksAdapter chosedDrinksAdapter = new ChosedDrinksAdapter(DrinksListActivity.this, chosedDrinks);
                    final ListView chosedList = new ListView(DrinksListActivity.this);
                    chosedList.setAdapter(chosedDrinksAdapter);
                    AlertDialog.Builder alert = new AlertDialog.Builder(DrinksListActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(32, 32, 16, 16);
                    chosedList.setLayoutParams(lp);
                    alert.setTitle("Chosed Drinks");
                    alert.setView(chosedList);
                    alert.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Chosed Drinks List is empty.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch(item.getItemId()) {
            case R.id.about:
                AlertDialog.Builder aboutAlert = new AlertDialog.Builder(DrinksListActivity.this);
                aboutAlert.setIcon(R.drawable.alert_icon);
                aboutAlert.setTitle("Caffeine per Day");
                aboutAlert.setMessage("Manage your caffeine levels with 'Caffeine per Day'.\nChose your drinks" +
                        " and see your daily caffeine dose.\n\nCopyrights© 2016 by Markov Gjorgji");
                aboutAlert.show();
                break;
            case R.id.search:
                item.setVisible(false);
                animateViewIn(searchLayout);
                searchLayout.setVisibility(RelativeLayout.VISIBLE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                break;
            case android.R.id.home:
                animateViewOut(searchLayout);
                searchLayout.setVisibility(RelativeLayout.GONE);
                String string = new String("");
                listAdapter.filterData(string);
                listAdapter.notifyDataSetChanged();
                search.setText("");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                Runnable rBack = new Runnable() {
                    @Override
                    public void run() {
                        invalidateOptionsMenu();
                    }
                };
                worker.schedule(rBack, 600, TimeUnit.MILLISECONDS);
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
            case R.id.reset:
                resetArray();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void animateViewIn(View view) {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(600);
        view.startAnimation(fadeIn);
    }
    public void animateViewOut(View view) {
        Animation fadeIn = new AlphaAnimation(1.0f, 0.0f);
        fadeIn.setDuration(600);
        view.startAnimation(fadeIn);
    }

    @Override
    public void onBackPressed() {
        if(searchLayout.getVisibility() == RelativeLayout.VISIBLE) {
            animateViewOut(searchLayout);
            searchLayout.setVisibility(RelativeLayout.GONE);
            String string = new String("");
            listAdapter.filterData(string);
            listAdapter.notifyDataSetChanged();
            search.setText("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            Runnable rBack = new Runnable() {
                @Override
                public void run() {
                    invalidateOptionsMenu();
                }
            };
            worker.schedule(rBack, 600, TimeUnit.MILLISECONDS);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            super.onBackPressed();
        }
    }

    public void resetArray() {
        for(int i=0;i<parentList.size();i++) {
            for(int j=0;j<parentList.get(i).getArray().size();j++) {
                parentList.get(i).getArray().get(j).setNumber(0);
            }
        }
        listAdapter.notifyDataSetChanged();
        chosedDrinks.clear();
        caffeine = 0;
    }
}

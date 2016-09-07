package com.sumera.meteorites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.sumera.meteorites.model.Meteorite;
import com.sumera.meteorites.model.MeteoritesCache;
import com.sumera.meteorites.model.MeteoritesRequest;
import com.sumera.meteorites.utils.DateFormater;
import com.sumera.meteorites.view.EmptyableFastScrollRecyclerView;
import com.sumera.meteorites.view.MeteoritesRecyclerViewAdapter;
import com.sumera.meteorites.view.RecyclerViewLineDivider;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MainActivity extends AppCompatActivity implements MeteoritesRecyclerViewAdapter.OnMeteoriteClickedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AlertDialog m_alertDialog;

    private ProgressDialog m_progressDialog;

    private boolean m_hasTwoPaneLayout;

    private MeteoritesRecyclerViewAdapter m_recyclerViewAdapter;

    private Subscription m_getMeteoritesSubscription;

    /**
     * When scroll is stopped show visible list items on map
     */
    public RecyclerView.OnScrollListener m_scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int first = manager.findFirstVisibleItemPosition();
                int last = manager.findLastVisibleItemPosition();

                MeteoriteDetailFragment fragment = getMeteoriteDetailFragment();
                if (fragment != null && !fragment.isInfoWindowShown()) {
                    showMeteoritesDetailFromToIndex(first, last);
                }
            }
        }
    };

    public Action1<List<Meteorite>> m_onMeteorites = new Action1<List<Meteorite>>() {
        @Override
        public void call(List<Meteorite> meteorites) {
            m_recyclerViewAdapter.setNewData(meteorites);
            setMeteoritesDataToDetailFragment(meteorites);
            hidePleaseWaitDialogIfIsShown();
            unsubscribe();
        }
    };

    public Action1<Throwable> m_onError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(TAG, throwable.toString());
            hidePleaseWaitDialogIfIsShown();
            showCantAccessDataDialog();
            unsubscribe();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupRecyclerView();

        startGetMeteoritesRequest();

        m_hasTwoPaneLayout = findViewById(R.id.meteorite_detail_container) != null;

        if (m_hasTwoPaneLayout) {
            initializeMeteoriteDetailFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unsubscribe();
    }

    @Override
    public void onMeteoriteClicked(Meteorite meteorite, int meteoritPosition) {
        if (m_hasTwoPaneLayout) {
            showMeteoriteOnPosition(meteoritPosition);
        } else {
            Intent intent = new Intent(this, MeteoriteDetailActivity.class);
            intent.putExtra(MeteoriteDetailFragment.METEORITE_ID_KEY, meteorite.getId());
            startActivity(intent);
        }
    }

    public void showPleaseWaitDialog() {
        m_progressDialog = new ProgressDialog(this);
        m_progressDialog.setIndeterminate(true);
        m_progressDialog.setMessage(R.string.network_request_title);
        m_progressDialog.setTitle(R.string.network_request_description);
        m_progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_progressDialog.setCancelable(false);
        m_progressDialog.show();
    }

    public void hidePleaseWaitDialogIfIsShown() {
        if (m_progressDialog != null) {
            m_progressDialog.dismiss();
            m_progressDialog = null;
        }
    }

    public void showCantAccessDataDialog() {
        m_alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.cant_access_data_title)
                .setMessage(R.string.cant_access_data_description)
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startGetMeteoritesRequest();
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .show();
    }

    private void initializeMeteoriteDetailFragment() {
        MeteoriteDetailFragment fragment = new MeteoriteDetailFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.meteorite_detail_container, fragment)
                .commit();
    }

    private void showMeteoriteOnPosition(int meteoritePosition) {
        MeteoriteDetailFragment fragment = getMeteoriteDetailFragment();
        if(fragment != null) {
            fragment.showMeteoriteWithInfoWindow(meteoritePosition);
        }
    }

    private void setMeteoritesDataToDetailFragment(List<Meteorite> meteorites) {
        MeteoriteDetailFragment fragment = getMeteoriteDetailFragment();
        if(fragment != null) {
            fragment.setMeteoritesData(meteorites);
        }
    }

    private MeteoriteDetailFragment getMeteoriteDetailFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.meteorite_detail_container);
        if(fragment != null && fragment instanceof MeteoriteDetailFragment) {
            return (MeteoriteDetailFragment) fragment;
        }

        return null;
    }

    private void showMeteoritesDetailFromToIndex(int from, int to) {
        MeteoriteDetailFragment fragment = getMeteoriteDetailFragment();
        if(fragment != null) {
            fragment.showMeteoritesDataFromToIndex(from, to);
        }
    }

    private void startGetMeteoritesRequest() {
        MeteoritesRequest request = new MeteoritesRequest();
        request.setActionBeforeAsyncTask(new Action0() {
            @Override
            public void call() {
                showPleaseWaitDialog();
            }
        });
        m_getMeteoritesSubscription = request.getMeteorites(m_onMeteorites, m_onError);
    }

    private void setupRecyclerView() {
        m_recyclerViewAdapter = new MeteoritesRecyclerViewAdapter(new ArrayList<Meteorite>());
        m_recyclerViewAdapter.setOnRecyclerViewItemClickListener(this);

        EmptyableFastScrollRecyclerView recyclerView = (EmptyableFastScrollRecyclerView) findViewById(R.id.meteorite_list);
        recyclerView.setAdapter(m_recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewLineDivider(this));

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        recyclerView.addOnScrollListener(m_scrollListener);

        View emptyView = findViewById(R.id.recycler_empty_view);
        recyclerView.setEmptyView(emptyView);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        long milisecondsFromLastUpdate = MeteoritesCache.getLastDatabaseUpdateTimeInMiliseconds();
        String format = "dd/MM/yyyy hh:mm:ss";
        String dateTime = DateFormater.getDateFromMilliseconds(milisecondsFromLastUpdate, format);
        toolbar.setSubtitle(getResources().getString(R.string.last_update) + dateTime);
    }

    private void unsubscribe() {
        if(m_getMeteoritesSubscription != null) {
            m_getMeteoritesSubscription.unsubscribe();
        }
    }

}

package com.sumera.meteorites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import rx.functions.Action1;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MainActivity extends AppCompatActivity implements
        MeteoritesRecyclerViewAdapter.OnMeteoriteClickedListener, Button.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar m_toolbar;

    private SwipeRefreshLayout m_swipeRefreshLayout;

    private ProgressDialog m_progressDialog;

    private boolean m_hasTwoPaneLayout;

    private MeteoritesRecyclerViewAdapter m_recyclerViewAdapter;

    private Subscription m_meteoritesSubscription;

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

            setupSwipeRefreshLayout();
            setMeteoritesDataToDetailFragment(meteorites);
            setLastUpdateTimeToToolbar();

            hidePleaseWaitDialogIfIsShown();
            hideSwipeRefreshLayoutIfIsShown();

            unsubscribe();
        }
    };

    public Action1<Throwable> m_onErrorDuringSwipeRefresh = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(TAG, throwable.toString());

            showCantAccessData();
            m_onErrorDuringRequest.call(throwable);
        }
    };

    public Action1<Throwable> m_onErrorDuringRequest = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(TAG, throwable.toString());

            if(MeteoritesCache.containsData()) {
                m_onMeteorites.call(MeteoritesCache.getMeteorites());
                return;
            }

            hidePleaseWaitDialogIfIsShown();
            hideSwipeRefreshLayoutIfIsShown();

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
            showMeteoriteDataInDetailFragment(meteoritPosition);
        } else {
            Intent intent = new Intent(this, MeteoriteDetailActivity.class);
            intent.putExtra(MeteoriteDetailFragment.METEORITE_ID_KEY, meteorite.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.try_again_button) {
            startGetMeteoritesRequest();
        }
    }

    private void showPleaseWaitDialog() {
        m_progressDialog = new ProgressDialog(this);
        m_progressDialog.setIndeterminate(true);
        m_progressDialog.setMessage(getResources().getString(R.string.network_request_description));
        m_progressDialog.setTitle(R.string.network_request_title);
        m_progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_progressDialog.setCancelable(false);
        m_progressDialog.show();
    }

    private void hidePleaseWaitDialogIfIsShown() {
        if (m_progressDialog != null) {
            m_progressDialog.dismiss();
            m_progressDialog = null;
        }
    }

    private void showCantAccessData() {
        final CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        Snackbar.make(coordinator, R.string.cant_access_data_title, Snackbar.LENGTH_LONG).show();
    }

    private void initializeMeteoriteDetailFragment() {
        MeteoriteDetailFragment fragment = new MeteoriteDetailFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.meteorite_detail_container, fragment)
                .commit();
    }

    private void showMeteoriteDataInDetailFragment(int meteoritePosition) {
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

    private void showMeteoritesDetailFromToIndex(int from, int to) {
        MeteoriteDetailFragment fragment = getMeteoriteDetailFragment();
        if(fragment != null) {
            fragment.showMeteoritesDataFromToIndex(from, to);
        }
    }

    private MeteoriteDetailFragment getMeteoriteDetailFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.meteorite_detail_container);
        if(fragment != null && fragment instanceof MeteoriteDetailFragment) {
            return (MeteoriteDetailFragment) fragment;
        }

        return null;
    }

    private void startGetMeteoritesRequest() {
        startGetMeteoritesRequest(false);
    }

    private void swipeRefreshMeteoritesDataFromNetwork() {
        startGetMeteoritesRequest(true);
    }

    private void startGetMeteoritesRequest(final boolean isSwipeRefresh) {
        if(m_meteoritesSubscription != null && !m_meteoritesSubscription.isUnsubscribed()) {
            return;
        }

        if(isSwipeRefresh) {
            m_meteoritesSubscription =
                    MeteoritesRequest.getMeteoritesFromNetwork(m_onMeteorites, m_onErrorDuringSwipeRefresh);
        } else {
            showPleaseWaitDialog();
            m_meteoritesSubscription =
                    MeteoritesRequest.getMeteorites(m_onMeteorites, m_onErrorDuringRequest);
        }
    }

    private void setupSwipeRefreshLayout() {
        m_swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        m_swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshMeteoritesDataFromNetwork();
                    m_swipeRefreshLayout.setRefreshing(true);
                }
            }
        );
    }

    private void setupRecyclerView() {
        m_recyclerViewAdapter = new MeteoritesRecyclerViewAdapter(new ArrayList<Meteorite>());
        m_recyclerViewAdapter.setOnRecyclerViewItemClickListener(this);

        EmptyableFastScrollRecyclerView recyclerView = (EmptyableFastScrollRecyclerView) findViewById(R.id.meteorite_list);
        recyclerView.setAdapter(m_recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewLineDivider(this));
        recyclerView.addOnScrollListener(m_scrollListener);

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(recyclerView);
        fastScroller.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Toast.makeText(getApplication(), "" + hasFocus, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        recyclerView.setFastScrollView(fastScroller);

        View emptyView = findViewById(R.id.recycler_empty_view);
        recyclerView.setEmptyView(emptyView);

        Button tryAgainButton = (Button) findViewById(R.id.try_again_button);
        tryAgainButton.setOnClickListener(this);
    }

    private void setupToolbar() {
        m_toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(m_toolbar);
        m_toolbar.setTitle(getTitle());

        setLastUpdateTimeToToolbar();
    }

    private void setLastUpdateTimeToToolbar() {
        long milisecondsFromLastUpdate = MeteoritesCache.getLastDatabaseUpdateTimeInMiliseconds();
        String format = "dd/MM/yyyy hh:mm:ss";
        String dateTime = DateFormater.getDateFromMilliseconds(milisecondsFromLastUpdate, format);
        m_toolbar.setSubtitle(getResources().getString(R.string.last_udpate) + " " + dateTime);
    }

    private void hideSwipeRefreshLayoutIfIsShown() {
        if(m_swipeRefreshLayout != null && m_swipeRefreshLayout.isRefreshing()) {
            m_swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void unsubscribe() {
        if(m_meteoritesSubscription != null) {
            m_meteoritesSubscription.unsubscribe();
        }
    }

}

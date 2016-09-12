package treehou.se.habit.ui.things;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHLink;
import se.treehou.ng.ohcommunicator.connector.models.OHThing;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.adapter.LinkAdapter;
import treehou.se.habit.ui.adapter.ThingAdapter;
import treehou.se.habit.util.ConnectionFactory;

public class ThingsListFragment extends RxFragment {

    private static final String TAG = ThingsListFragment.class.getSimpleName();

    private static final String ARG_SERVER = "ARG_SERVER";


    @Inject ConnectionFactory connectionFactory;

    @BindView(R.id.list) RecyclerView listView;
    @BindView(R.id.empty) TextView emptyView;

    private ThingAdapter adapter;
    private Realm realm;
    private ServerDB server;
    private Unbinder unbinder;

    /**
     * Create fragment where user can select sitemap.
     *
     * @return Fragment
     */
    public static ThingsListFragment newInstance(long serverId) {
        ThingsListFragment fragment = new ThingsListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, serverId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ThingsListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HabitApplication) getActivity().getApplication()).component().inject(this);

        realm = Realm.getDefaultInstance();
        long serverId = getArguments().getLong(ARG_SERVER);
        server = ServerDB.load(realm, serverId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_things_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ThingAdapter();
        adapter.setItemListener(new ThingAdapter.ItemListener() {
            @Override
            public void onItemClickListener(OHThing item) {
                openThingDialog(item);
            }

            @Override
            public boolean onItemLongClickListener(OHThing item) {
                return false;
            }
        });
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * Open thing page.
     * @param thing the thing to open.
     */
    private void openThingDialog(OHThing thing){

    }

    /**
     * Setup actionbar.
     */
    private void setupActionBar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.things);
    }

    /**
     * Clears list of sitemaps.
     */
    private void clearList() {
        emptyView.setVisibility(View.VISIBLE);
        adapter.clear();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadThings();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Load servers from database and request their sitemaps.
     */
    private void loadThings(){
        connectionFactory.createServerHandler(server.toGeneric(), getContext())
                .requestThingsRx()
                .filter(ohThings -> ohThings.size() > 0)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(ohThings -> {
                    clearList();
                    emptyView.setVisibility(View.GONE);
                    adapter.addAll(ohThings);
                }, throwable -> {
                    Log.e(TAG, "Error loading things", throwable);
                });
    }
}

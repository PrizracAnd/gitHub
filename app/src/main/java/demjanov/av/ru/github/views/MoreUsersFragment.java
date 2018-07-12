package demjanov.av.ru.github.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import demjanov.av.ru.github.R;
import demjanov.av.ru.github.presenters.MainView;
import demjanov.av.ru.github.presenters.PresenterMoreUsers;

public class MoreUsersFragment extends Fragment implements MoreUsersAdapter.MoreUsersCall, MainView {

    @BindView(R.id.more_users_recycle)
    RecyclerView recyclerView;

    @BindView(R.id.progressBarMore)
    ProgressBar progressBar;

    private View view;
    private PresenterMoreUsers presenter;
    private MoreUsersAdapter myAdapter;
    private ClickListenerUsers mainActivity;

    public interface ClickListenerUsers{
        void onClickUsers(String userName);
    }

    @Override
    public void onAttach(Context context) {
        mainActivity = (ClickListenerUsers) context;
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        this.view = inflater.inflate(R.layout.fragment_more_users, viewGroup, false);
//        ButterKnife.bind(view);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        initializeElements(this.view);
    }



    /////////////////////////////////////////////////////
    // Method initializeElements
    ////////////////////////////////////////////////////
    private void initializeElements(View view) {
//        ButterKnife.bind(this.getActivity());

        progressBar = (ProgressBar)view.findViewById(R.id.progressBarMore);
        recyclerView = (RecyclerView)view.findViewById(R.id.more_users_recycle);

        //---Presenter_begin---
        this.presenter = new PresenterMoreUsers(this, view.getContext());
        this.presenter.startLoadData();
        //---Presenter_end---


        //---RecyclerView_begin---
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        myAdapter = new MoreUsersAdapter(this.presenter.getResults(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
        //---RecyclerView_end---

    }


    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onCallUser(String userName) {
        mainActivity.onClickUsers(userName);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void endLoad() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setError(int number, @Nullable String message) {

    }

    @Override
    public void setData(int dataType) {

    }
    //-----End-------------------------------------------


    @Override
    public void onDestroy() {

        this.presenter.destroy();

        super.onDestroy();
    }
}

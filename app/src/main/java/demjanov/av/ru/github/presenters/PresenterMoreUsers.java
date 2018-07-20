package demjanov.av.ru.github.presenters;


import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;


import demjanov.av.ru.github.db.DaggerInjectorRealm;
import demjanov.av.ru.github.db.QueryUsers;
import demjanov.av.ru.github.db.RealmModelUser;
import demjanov.av.ru.github.models.RetrofitModel;
import demjanov.av.ru.github.network.Caller;
import demjanov.av.ru.github.views.MoreUsersFragment;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class PresenterMoreUsers {
    //-----Class variables begin-------------------------

    private MoreUsersFragment moreUsersFragment;

    private List<RetrofitModel> listRetrofitModel = new ArrayList<RetrofitModel>();
    private int messageType;


    private Disposable disposable;

    private QueryUsers queryUsers;

    private Context context;


    @Inject
    Realm realm;

    //-----Class variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public PresenterMoreUsers(@NonNull MoreUsersFragment moreUsersFragment, Context context) {
        this.moreUsersFragment = moreUsersFragment;
        this.context = context;

        DaggerInjectorRealm.builder()
                .contextProvider(new ContextProvider(this.context))
                .build()
                .injectToPresenterMoreUsers(this);

        this.queryUsers = new QueryUsers(this.realm);

    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public RealmResults<RealmModelUser> getResults(){
        this.moreUsersFragment.startLoad();
        this.queryUsers.selectAllUsers();
        while (this.queryUsers.isTransact());

        return this.queryUsers.getModelUsersList();
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method startLoadData
    ////////////////////////////////////////////////////
    public void startLoadData(){
        this.moreUsersFragment.startLoad();

        refreshListRetrofitModel();

        Completable completable = Completable.create(emitter -> {
            Caller caller = new Caller(this.context, this.listRetrofitModel);
            caller.downloadUsers();

            while (caller.isDownloads()) ;

            if (caller.getCodeMessage() == Caller.ALL_GUT) {
                emitter.onComplete();
            } else {
                this.messageType = caller.getCodeMessage();
                emitter.onError(new IOException(caller.getMessage()));
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        this.disposable = completable.subscribeWith(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                moreUsersFragment.endLoad();

//                    mainActivity.setData(Caller.MORE_USERS);
                queryUsers.deleteAllUsers();
                queryUsers.insertUsersData(listRetrofitModel);

            }

            @Override
            public void onError(Throwable e) {
                moreUsersFragment.endLoad();
                moreUsersFragment.setError(messageType, e.getMessage());

            }
        });

    }

    /////////////////////////////////////////////////////
    // Method refreshListRetrofitModel
    ////////////////////////////////////////////////////
    private void refreshListRetrofitModel(){
        if(this.listRetrofitModel != null && !this.listRetrofitModel.isEmpty()){
            this.listRetrofitModel.removeAll(this.listRetrofitModel);
        }
        this.listRetrofitModel = new ArrayList<RetrofitModel>();
    }


    /////////////////////////////////////////////////////
    // Method destroy
    ////////////////////////////////////////////////////
    public void destroy(){
        if (this.disposable != null && this.disposable.isDisposed()) {
            this.disposable.dispose();
        }

        this.queryUsers.destroy();
        this.realm.close();
    }


}

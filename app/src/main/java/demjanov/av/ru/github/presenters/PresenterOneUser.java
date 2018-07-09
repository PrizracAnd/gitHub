package demjanov.av.ru.github.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import demjanov.av.ru.github.db.DaggerInjectorRealm;
import demjanov.av.ru.github.db.QueryUsers;
import demjanov.av.ru.github.models.RetrofitModel;
import demjanov.av.ru.github.network.Caller;
import demjanov.av.ru.github.views.MoreUsersFragment;
import demjanov.av.ru.github.views.OneUsersFragment;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class PresenterOneUser {
    //-----Class variables begin-------------------------

    private OneUsersFragment oneUsersFragment;

    private List<RetrofitModel> listRetrofitModel = new ArrayList<RetrofitModel>();
    private int messageType;


    private Disposable disposable;

//    private QueryUsers queryUsers;

    private Context context;


//    @Inject
//    Realm realm;

    //-----Class variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public PresenterOneUser(@NonNull OneUsersFragment oneUsersFragment, Context context) {
        this.oneUsersFragment = oneUsersFragment;
        this.context = context;
    }

    /////////////////////////////////////////////////////
    // Method startLoadData
    ////////////////////////////////////////////////////
    public void startLoadData(String userName){
        this.oneUsersFragment.startLoad();

        refreshListRetrofitModel();

        Completable completable = Completable.create(emitter -> {
            Caller caller = new Caller(this.context, this.listRetrofitModel);
            caller.downloadUser(userName);

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
                oneUsersFragment.endLoad();
                oneUsersFragment.setData(Caller.ONE_USER);
            }

            @Override
            public void onError(Throwable e) {
                oneUsersFragment.endLoad();
                oneUsersFragment.setError(messageType, e.getMessage());

            }
        });

    }

    /////////////////////////////////////////////////////
    // Method refreshListRetrofitModel
    ////////////////////////////////////////////////////
    private void refreshListRetrofitModel(){
        if(this.listRetrofitModel != null && !this.listRetrofitModel.isEmpty()){
            this.listRetrofitModel.removeAll(this.listRetrofitModel);
            this.listRetrofitModel = new ArrayList<RetrofitModel>();
        }
    }


    /////////////////////////////////////////////////////
    // Method destroy
    ////////////////////////////////////////////////////
    public void destroy(){
        if (this.disposable != null && this.disposable.isDisposed()) {
            this.disposable.dispose();
        }

//        this.queryUsers.destroy();
//        this.realm.close();
    }


    /////////////////////////////////////////////////////
    // Methods getData
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    //-----Get Data of user begin------------------------

    public String getDataUserLogin(){
        if(this.listRetrofitModel.size() > 0){
            return this.listRetrofitModel.get(0).getLogin();
        }else return null;
    }

    public String getDataUserID(){
        if(this.listRetrofitModel.size() > 0){
            return this.listRetrofitModel.get(0).getId();
        }else return null;
    }

    public String getDataUserAvatarUrl(){
        if(this.listRetrofitModel.size() > 0){
            return this.listRetrofitModel.get(0).getAvatarUrl();
        }else return null;
    }
    //-----Get Data of user end--------------------------

    //-----End-------------------------------------------
}

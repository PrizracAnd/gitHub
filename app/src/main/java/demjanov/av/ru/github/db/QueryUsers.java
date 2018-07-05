package demjanov.av.ru.github.db;

import android.support.annotation.NonNull;
import android.util.Log;


import java.util.List;

import demjanov.av.ru.github.models.RetrofitModel;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class QueryUsers {
    private final static String REALM_DB = "REALM_DB:";

    private Realm realm;

    private RealmResults<RealmModelUser> modelUsersList;
    private Disposable disposable;
    private boolean isTransact = false;
    private boolean isSuccess;


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////

    public QueryUsers(Realm realm) {
        this.realm = realm;
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public RealmResults<RealmModelUser> getModelUsersList() {
        return this.modelUsersList;
    }

    public boolean isTransact() {
        return isTransact;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method insertUsersData
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void insertUsersData(List<RetrofitModel> listUsers){
        this.isTransact = true;
        Completable completable = Completable.create(emitter ->{
            String curLogin;
            String curUserID;
            String curAvatarUrl;
            Realm realm = this.realm;
            for (RetrofitModel item : listUsers) {
                curLogin = item.getLogin();
                curUserID = item.getId();
                curAvatarUrl = item.getAvatarUrl();
                try {
                    realm.beginTransaction();
                    RealmModelUser realmModelUser = realm.createObject(RealmModelUser.class);
                    realmModelUser.setLogin(curLogin);
                    realmModelUser.setId(curUserID);
                    realmModelUser.setAvatarUrl(curAvatarUrl);
                    realm.commitTransaction();
                }catch (Exception e) {
                    realm.cancelTransaction();
                    realm.close();
                    emitter.onError(e);
                }
            }
            realm.close();
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.disposable = completable.subscribeWith(createObserver());
    }


    /////////////////////////////////////////////////////
    // Method deleteAllUsers
    ////////////////////////////////////////////////////
    public void deleteAllUsers(){
        this.isTransact = true;
        Completable completable = Completable.create(emitter -> {
            Realm realm = this.realm;
            try{
                final RealmResults<RealmModelUser> listResults = realm.where(RealmModelUser.class).findAll();
                realm.executeTransaction(realm1 -> listResults.deleteAllFromRealm());
                emitter.onComplete();
            }catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.disposable = completable.subscribeWith(createObserver());
    }


    /////////////////////////////////////////////////////
    // Methods selectAllUsers
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void selectAllUsers(){
        this.isTransact = true;
        Single single = Single.create(emitter -> {
            Realm realm = this.realm;
            try {
                RealmResults<RealmModelUser> listResult = realm.where(RealmModelUser.class).findAll();
                emitter.onSuccess(listResult);
            }catch (Exception e){
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        this.disposable = (Disposable) single.subscribeWith(new DisposableSingleObserver<RealmResults<RealmModelUser>>() {


            @Override
            public void onSuccess(RealmResults<RealmModelUser> list) {
                modelUsersList = list;
                isTransact = false;
                isSuccess = true;

            }

            @Override
            public void onError(Throwable e) {
                modelUsersList = null;
                isTransact = false;
                isSuccess = false;
                Log.d(REALM_DB, e.getMessage());
            }
        });
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method createObserver
    ////////////////////////////////////////////////////
    @NonNull
    @org.jetbrains.annotations.Contract(value = " -> !null", pure = true)
    private DisposableCompletableObserver createObserver(){
        return new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                isTransact = false;
                isSuccess = true;

            }

            @Override
            public void onError(Throwable e) {
                isTransact = false;
                isSuccess = false;
                Log.d(REALM_DB, e.getMessage());
            }
        };
    }


    /////////////////////////////////////////////////////
    // Method destroy
    ////////////////////////////////////////////////////
    public void destroy() {
        if (this.disposable != null && this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }
}

package demjanov.av.ru.github.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import demjanov.av.ru.github.db.RealmModelUser;
import demjanov.av.ru.github.models.RetrofitModel;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmSupportDB {
    private final static String REALM_DB = "REALM_DB:";

    private Context context;

    private List<RetrofitModel> retrofitModelList;
    private Disposable disposable;
    private boolean isTransact = false;
    private boolean isSuccess;


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////

    public RealmSupportDB(Context context) {
        this.context = context;
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public List<RetrofitModel> getRetrofitModelList() {
        return retrofitModelList;
    }

    public boolean isTransact() {
        return isTransact;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method init
    ////////////////////////////////////////////////////
    public void init(){
        Realm.init(this.context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
    }


    /////////////////////////////////////////////////////
    // Methods insert
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void insertUsersData(List<RetrofitModel> listUsers){
        this.isTransact = true;
        Completable completable = Completable.create(emitter ->{
            String curLogin;
            String curUserID;
            String curAvatarUrl;
            Realm realm = Realm.getDefaultInstance();
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
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Methods delete
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void deleteAllUsers(){
        this.isTransact = true;
        Completable completable = Completable.create(emitter -> {
            try(Realm realm = Realm.getDefaultInstance()) {
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
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Methods select
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void selectAllUsers(){
        this.isTransact = true;
        Single single = Single.create(emitter -> {
            try (Realm realm = Realm.getDefaultInstance()){
                RealmResults<RealmModelUser> listResult = realm.where(RealmModelUser.class).findAll();
                List<RetrofitModel> retrofitModelList = new ArrayList<>();
                for(RealmModelUser item: listResult){
                    RetrofitModel retrofitModel = new RetrofitModel();
                    retrofitModel.setLogin(item.getLogin());
                    retrofitModel.setId(item.getId());
                    retrofitModel.setAvatarUrl(item.getAvatarUrl());
                    retrofitModelList.add(retrofitModel);
                }
                emitter.onSuccess(retrofitModelList);
            }catch (Exception e){
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        this.disposable = (Disposable) single.subscribeWith(new DisposableSingleObserver<List<RetrofitModel>>() {


            @Override
            public void onSuccess(List<RetrofitModel> list) {
                retrofitModelList = list;
                isTransact = false;
                isSuccess = true;

            }

            @Override
            public void onError(Throwable e) {
                retrofitModelList = null;
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

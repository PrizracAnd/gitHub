package demjanov.av.ru.github.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Component;
import dagger.internal.DaggerCollections;
import demjanov.av.ru.github.MainActivity;
import demjanov.av.ru.github.R;
import demjanov.av.ru.github.db.DaggerInjectorRealmInit;
import demjanov.av.ru.github.db.InjectorRealmInit;
import demjanov.av.ru.github.db.RealmInit;
import demjanov.av.ru.github.db.RealmSupportDB;
import demjanov.av.ru.github.models.RetrofitModel;
import demjanov.av.ru.github.models.UserModel;
import demjanov.av.ru.github.network.Caller;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class Presenter {


    //-----Class variables begin-------------------------
    private MainActivity mainActivity;


    private List<RetrofitModel> listRetrofitModel = new ArrayList<RetrofitModel>();
    private String baseUrl;
    private int messageType;
    private UserModel userModel = new UserModel();
    private Context context;

    private Disposable disposable;

    private RealmSupportDB realmSupportDB;

    @Inject
    RealmInit realmInit;
    private Realm realm;

    //-----Class variables end---------------------------

    /////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////
    //-----Constructors begin----------------------------

    public Presenter(@NonNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();


        DaggerInjectorRealmInit.create().injectToPresenter(this);
        this.realmInit.init(this.context);
        this.realm = this.realmInit.getRealm();


        this.realmSupportDB = new RealmSupportDB(this.context); //FIXME перейти на новый класс запросов
//        this.realmSupportDB.init();

    }

    public Presenter(@NonNull MainActivity mainActivity, byte[] userName) {
        this(mainActivity);
        this.userModel.setUserName(userName);
    }
    //-----Constructors end------------------------------



    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void setUserName(byte[] userName){
        this.userModel.setUserName(userName);
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method startLoadData
    ////////////////////////////////////////////////////
    public void startLoadData(){
//        if(this.userModel.getUserName().length > 0) {
        mainActivity.startLoad();

        refreshListRetrofitModel();

        Completable completable = Completable.create(emitter -> {
            Caller caller = new Caller(context, context.getResources().getString(R.string.baseUrl), this.userModel, this.listRetrofitModel);
//            caller.download();
            if(this.userModel.getUserName().length > 0) {
                caller.downloadUser();
            }else {
                caller.downloadUsers();
            }
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
                mainActivity.endLoad();
                if(listRetrofitModel.size() > 1) {
                    mainActivity.setData(Caller.MORE_USERS);
                    realmSupportDB.deleteAllUsers();
                    realmSupportDB.insertUsersData(listRetrofitModel);
                }else {
                    mainActivity.setData(Caller.ONE_USER);
                }
            }

            @Override
            public void onError(Throwable e) {
                mainActivity.endLoad();
                mainActivity.setError(messageType, e.getMessage());

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

        this.realmSupportDB.destroy();

        this.userModel.shred();
    }


    /////////////////////////////////////////////////////
    // Methods getData
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    //-----Get Data of user begin------------------------
    public String getDataUsers(){
        StringBuilder sb = new StringBuilder();

        for (RetrofitModel item: this.listRetrofitModel){

            sb.append(item.getLogin());
            sb.append("\n");
            sb.append(item.getId());
            sb.append("\n\n");
        }
        sb.delete(sb.length() - 3, sb.length() - 1);
        return sb.toString();
    }

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

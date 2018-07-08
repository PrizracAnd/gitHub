package demjanov.av.ru.github.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import demjanov.av.ru.github.MainActivity;
import demjanov.av.ru.github.db.DaggerInjectorRealm;
import demjanov.av.ru.github.db.QueryUsers;
import demjanov.av.ru.github.models.RetrofitModel;
import demjanov.av.ru.github.models.UserModel;
import demjanov.av.ru.github.views.MoreUsersFragment;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

public class PresenterMoreUsers {
    //-----Class variables begin-------------------------
    private MainActivity mainActivity;
    private MoreUsersFragment moreUsersFragment;

    private List<RetrofitModel> listRetrofitModel = new ArrayList<RetrofitModel>();
    private String baseUrl;
    private int messageType;
    private UserModel userModel = new UserModel();
    private Context context;

    private Disposable disposable;

    private QueryUsers queryUsers;

    @Inject
    Realm realm;

    //-----Class variables end---------------------------

    /////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////
    //-----Constructors begin----------------------------

    public PresenterMoreUsers(@NonNull MoreUsersFragment moreUsersFragment) {
        this.moreUsersFragment = moreUsersFragment;

        DaggerInjectorRealm.builder()
                .contextProvider(new ContextProvider(this.moreUsersFragment.getContext()))
                .build()
                .injectToPresenterMoreUsers(this);

        this.queryUsers = new QueryUsers(this.realm);

    }


}

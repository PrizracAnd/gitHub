package demjanov.av.ru.github.db;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import demjanov.av.ru.github.presenters.ContextProvider;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module(includes = ContextProvider.class)
public class RealmInit {

    @Provides
    public Realm getRealm(Context context) {
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);

        return Realm.getDefaultInstance();
    }

//    @Provides
//    RealmInit getRealmInit(){
//        return this;
//    }
}

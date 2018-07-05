package demjanov.av.ru.github.db;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
@Module
public class RealmInit {
    private Realm realm;

    public void init(Context context) {

        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);

        this.realm = Realm.getDefaultInstance();
    }


    public Realm getRealm() {
        return realm;
    }

    @Provides
    RealmInit getRealmInit(Context context){
        return this;
    }
}

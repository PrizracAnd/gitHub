package demjanov.av.ru.github.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import dagger.Module;
import dagger.Provides;
import demjanov.av.ru.github.presenters.ContextProvider;

/**
 * Created by demjanov on 06.07.2018.
 */
@Module(includes = ContextProvider.class)
public class NetworkInfoProvider {

    @Provides
    public NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }
}

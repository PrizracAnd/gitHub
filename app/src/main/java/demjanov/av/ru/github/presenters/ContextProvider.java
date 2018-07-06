package demjanov.av.ru.github.presenters;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by demjanov on 06.07.2018.
 */

@Module
public class ContextProvider {
    Context context;

    public ContextProvider(Context context) {
        this.context = context;
    }

    @Provides
    public Context getContext() {
        return context.getApplicationContext();
    }
}

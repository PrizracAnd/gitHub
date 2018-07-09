package demjanov.av.ru.github.network;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class CreaterRestAPI {
    public final static String BASE_URL = "https://api.github.com/";
    @Provides
    RestAPI getRestAPI(){
        Retrofit retrofit = null;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RestAPI.class);
    }

}

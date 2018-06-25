package demjanov.av.ru.github.network;

import java.util.List;

import demjanov.av.ru.github.models.RetrofitModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestAPIforUser {
    @GET("users/{user}")
    Call<List<RetrofitModel>> loadUsers(@Path("user") String user);

}

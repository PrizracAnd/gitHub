package demjanov.av.ru.github.network;

import java.util.List;

import demjanov.av.ru.github.models.RetrofitModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RestAPI {
    @GET("users")
    Call<List<RetrofitModel>> loadUsers();
}

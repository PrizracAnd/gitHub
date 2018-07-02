package demjanov.av.ru.github.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;


import java.util.List;

import demjanov.av.ru.github.models.RetrofitModel;
import demjanov.av.ru.github.models.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Caller {

    //-----Constants begin-------------------------------
    //-----Messages constants begin----------------------
    public final static int NOT_MESSAGE = 0;
    public final static int ALL_GUT = 200;
    public final static int NO_RETROFIT = 1;
    public final static int NO_CALL = 2;
    public final static int NO_CONNECTED = 3;
    public final static int ON_FAILURE = 4;
    public final static int RESPONSE_ERROR = 5;
    public final static int NOT_LOADING_DATA = 6;

    public final static String[] titleMessage = {
           "NOT_MESSAGE: "
           , "NO_RETROFIT: "
           , "NO_CALL: "
           , "NO_CONNECTED: "
           , "ON_FAILURE: "
           , "RESPONSE_ERROR: "
           , "NOT_LOADING_DATA: "
    };
    //-----Messages constants end------------------------

    //-----Queries types constants begin-----------------
    public final static int MORE_USERS = 0;
    public final static int ONE_USER = 1;
    //-----Queries types constants end-------------------
    //-----Constants end---------------------------------


    //-----Messages variables begin----------------------
    private int codeMessage = NOT_MESSAGE;      //-- code of message
    private String message;                     //-- string of message
    //-----Messages variables end------------------------


    //-----Class variables begin-------------------------
    private Context context;
    private String baseUrl;
    private int queryType;
    private UserModel userModel;
    private List<RetrofitModel> listRetrofitModel;
    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    boolean isDownloads;
    //-----Other variables end---------------------------



    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////

    public Caller(Context context, String baseUrl, int queryType, UserModel userModel, List<RetrofitModel> listRetrofitModel) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.queryType = queryType;
        this.userModel = userModel;
        this.listRetrofitModel = listRetrofitModel;
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public int getCodeMessage() {
        return codeMessage;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isDownloads() {
        return isDownloads;
    }

    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method setMessageInfo
    ////////////////////////////////////////////////////
    private void setMessageInfo(int codeMessage, @Nullable String message){
        this.codeMessage = codeMessage;
        this.message = message;
    }


    /////////////////////////////////////////////////////
    // Method createCall
    ////////////////////////////////////////////////////
    @Nullable
    private Call createCall(int callNumber){
        Call call;
        try {
            Retrofit retrofit = null;
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            switch (callNumber) {
                case MORE_USERS:
                    call = retrofit.create(RestAPI.class)
                            .loadUsers();
                    break;
                case ONE_USER:
                    call = retrofit.create(RestAPI.class)
                            .loadUsers(new String(this.userModel.getUserName()));
                    break;
                default:
                    setMessageInfo(NO_CALL, null);
                    return null;
            }
        }catch (Exception e){
            setMessageInfo(NO_RETROFIT, e.getMessage());
            return null;
        }

        return call;
    }

    private boolean isConnected(){
        NetworkInfo networkInfo = ((ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /////////////////////////////////////////////////////
    // Methods download
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void download(){
       switch (this.queryType){
           case MORE_USERS:
               downloadUsers();
               break;
           case ONE_USER:
               downloadUser();
               break;
           default:
               break;
       }
    }


    /////////////////////////////////////////////////////
    // Method download
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void downloadUsers() {
        this.isDownloads = true;
        Call call = createCall(this.queryType);

        if(isConnected()){
            if(call != null) {
                call.enqueue(new Callback<List<RetrofitModel>>() {
                    @Override
                    public void onResponse(Call<List<RetrofitModel>> call, Response<List<RetrofitModel>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                for (int i = 0; i < response.body().size(); i++){
                                    RetrofitModel retrofitModel = response.body().get(i);
                                    listRetrofitModel.add(retrofitModel);
                                }
//                                listRetrofitModel.addAll(response.body());
                                setMessageInfo(ALL_GUT, null);
                            } else {
                                setMessageInfo(NOT_LOADING_DATA, null);
                            }
                        } else {
                            setMessageInfo(RESPONSE_ERROR, "" + response.code());
                        }
                        isDownloads = false;
                    }

                    @Override
                    public void onFailure(Call<List<RetrofitModel>> call, Throwable t) {
                        setMessageInfo(ON_FAILURE, t.getMessage());
                        isDownloads = false;
                    }
                });
            }else isDownloads = false;
        }else {
            setMessageInfo(NO_CONNECTED, null);
            isDownloads = false;
        }
    }

    public void downloadUser(){
        this.isDownloads = true;
        Call call = createCall(this.queryType);

        if(isConnected()){
            if(call != null) {
                call.enqueue(new Callback<RetrofitModel>() {
                    @Override
                    public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                RetrofitModel rm = response.body();
                                listRetrofitModel.add(rm);
                                setMessageInfo(ALL_GUT, null);
                            }else {
                                setMessageInfo(NOT_LOADING_DATA, null);
                            }
                        }else {
                            setMessageInfo(RESPONSE_ERROR, "" + response.code());
                        }

                        isDownloads = false;
                    }

                    @Override
                    public void onFailure(Call<RetrofitModel> call, Throwable t) {
                        setMessageInfo(ON_FAILURE, t.getMessage());
                        isDownloads = false;
                    }
                });
            }else isDownloads = false;
        }else {
            setMessageInfo(NO_CONNECTED, null);
            isDownloads = false;
        }

    }
    //-----End-------------------------------------------


}

package demjanov.av.ru.github;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import demjanov.av.ru.github.network.Caller;
import demjanov.av.ru.github.presenters.MainView;
import demjanov.av.ru.github.presenters.Presenter;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

public class MainActivity extends AppCompatActivity implements MainView {
    @BindView(R.id.editText)                    //-- в 5 утра выбесил findViewById :-))
    EditText editText;

    @BindView(R.id.tvLoad)
    TextView textView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.btnLoad)
    Button buttonLoad;

    @BindView(R.id.imageView)
    ImageView imageView;

    private Presenter presenter;



    /////////////////////////////////////////////////////
    // Method onCreate
    ////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElements();

    }


    /////////////////////////////////////////////////////
    // Method initializeElements
    ////////////////////////////////////////////////////
    private void initializeElements() {
        ButterKnife.bind(this);
//        buttonLoad.setOnClickListener(this::onClick);

        presenter = new Presenter(this);

        setTextButtonLoad();
    }


    /////////////////////////////////////////////////////
    // Method onDestroy
    ////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        this.presenter.destroy();

        super.onDestroy();
    }


    /////////////////////////////////////////////////////
    // Methods of interface Main_View
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void endLoad() {
        progressBar.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setError(int number, @Nullable String message) {
        switch (number){
            case Caller.NO_CALL:
                Toast.makeText(this, getResources().getString(R.string.no_call),Toast.LENGTH_LONG).show();
                break;
            case Caller.NO_CONNECTED:
                Toast.makeText(this, getResources().getString(R.string.no_connect),Toast.LENGTH_LONG).show();
                break;
            case Caller.NOT_LOADING_DATA:
                textView.setText(getResources().getString(R.string.not_loading_data));
                break;
            default:
                Log.d(Caller.titleMessage[number], message);
                break;
        }
    }

    @Override
    public void setData(int dataType) {
        switch (dataType){
            case Caller.ONE_USER:
                textView.setText(getResources().getString(R.string.user_id) + presenter.getDataUserID());
                Glide.with(this)
                        .load(presenter.getDataUserAvatarUrl())
                        .into(imageView);
                imageView.setVisibility(View.VISIBLE);
                break;
            case Caller.MORE_USERS:
                textView.setText(presenter.getDataUsers());
                imageView.setVisibility(View.GONE);
                break;
        }

    }
    //-----End-------------------------------------------

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btnLoad:
//                presenter.setUserName((editText.getText()).toString().getBytes());
//                presenter.startLoadData();
//                break;
//            default:
//                break;
//
//        }
//    }


    /////////////////////////////////////////////////////
    // Methods of buttonLoad
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @OnClick(R.id.btnLoad)
    private void onClickButtonLoad(){
        presenter.setUserName((editText.getText()).toString().getBytes());
        presenter.startLoadData();
    }

    private void setTextButtonLoad(){
        if(editText.getText().toString().isEmpty()){
            buttonLoad.setText(getResources().getString(R.string.download_users));
        }else {
            buttonLoad.setText(getResources().getString(R.string.download_page));
        }
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method afterTextChanged for editText
    ////////////////////////////////////////////////////
    @OnTextChanged(value = R.id.editText, callback = AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        setTextButtonLoad();
    }
}

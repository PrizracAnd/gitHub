package demjanov.av.ru.github;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import demjanov.av.ru.github.network.Caller;
import demjanov.av.ru.github.presenters.Main_View;
import demjanov.av.ru.github.presenters.Presenter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Main_View {
    @BindView(R.id.editText)                    //-- в 5 утра выбесил findViewById :-))
    EditText editText;

    @BindView(R.id.tvLoad)
    TextView textView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.btnLoad)
    Button buttonLoad;

    private Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElements();

    }

    private void initializeElements() {
        ButterKnife.bind(this);
        buttonLoad.setOnClickListener(this::onClick);

        presenter = new Presenter(this);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
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
    public void setData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLoad:
                presenter.setUserName((editText.getText()).toString().getBytes());
                presenter.startLoadData();
                break;
            default:
                break;

        }
    }
}

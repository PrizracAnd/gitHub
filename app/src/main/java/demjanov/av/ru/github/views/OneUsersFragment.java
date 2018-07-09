package demjanov.av.ru.github.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import demjanov.av.ru.github.R;
import demjanov.av.ru.github.network.Caller;
import demjanov.av.ru.github.presenters.MainView;
import demjanov.av.ru.github.presenters.PresenterOneUser;

public class OneUsersFragment extends Fragment implements MainView {

    @BindView(R.id.login_text)
    TextView loginText;

    @BindView(R.id.avatar_view)
    ImageView avatarImage;

    @BindView(R.id.info_text)
    TextView infoText;

    @BindView(R.id.progressBarOne)
    ProgressBar progressBar;

    private String userName;
    private PresenterOneUser presenter;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        this.rootView = inflater.inflate(R.layout.fragment_one_users, viewGroup, false);

        initializeElements(this.rootView);

        return this.rootView;
    }



    /////////////////////////////////////////////////////
    // Method initializeElements
    ////////////////////////////////////////////////////
    private void initializeElements(View view) {
        ButterKnife.bind(view);

        presenter = new PresenterOneUser(this, view.getContext());
        presenter.startLoadData(this.userName);

    }





    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void startLoad() {
        loginText.setVisibility(View.GONE);
        infoText.setVisibility(View.GONE);
        avatarImage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void endLoad() {
        loginText.setVisibility(View.VISIBLE);
        infoText.setVisibility(View.VISIBLE);
        avatarImage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void setError(int number, @Nullable String message) {
        loginText.setText(this.userName);

        Context context = this.rootView.getContext().getApplicationContext();

        switch (number){
            case Caller.NO_CALL:

                Toast.makeText(context, context.getResources().getString(R.string.no_call), Toast.LENGTH_LONG).show();
                break;
            case Caller.NO_CONNECTED:
                Toast.makeText(context, context.getResources().getString(R.string.no_connect),Toast.LENGTH_LONG).show();
                break;
            case Caller.NOT_LOADING_DATA:
                infoText.setText(getResources().getString(R.string.not_loading_data));
                break;
            default:
                Log.d(Caller.titleMessage[number], message);
                break;
        }

    }

    @Override
    public void setData(int dataType) {
        loginText.setText(presenter.getDataUserLogin());
        Glide.with(this)
                .load(presenter.getDataUserAvatarUrl())
                .into(avatarImage);
        avatarImage.setVisibility(View.VISIBLE);

        infoText.setText(getResources().getString(R.string.user_id) + presenter.getDataUserID());


    }
    //-----End-------------------------------------------

    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //-----End-------------------------------------------
}

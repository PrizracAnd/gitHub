package demjanov.av.ru.github;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import demjanov.av.ru.github.views.MoreUsersFragment;
import demjanov.av.ru.github.views.OneUsersFragment;


public class MainActivity extends AppCompatActivity implements MoreUsersFragment.ClickListenerUsers{

    private FragmentManager fm;

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
        fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragment = new MoreUsersFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


    /////////////////////////////////////////////////////
    // Method onDestroy
    ////////////////////////////////////////////////////
    @Override
    public void onDestroy() {


        super.onDestroy();
    }


    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    @Override
    public void onClickUsers(String userName) {
        OneUsersFragment oneUsersFragment = new OneUsersFragment();
        oneUsersFragment.setUserName(userName);

        fm.beginTransaction()
                .replace(R.id.fragment_container, oneUsersFragment)
                .addToBackStack(null)
                .commit();
    }

    //-----End-------------------------------------------

}

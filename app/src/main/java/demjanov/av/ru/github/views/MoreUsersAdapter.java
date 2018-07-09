package demjanov.av.ru.github.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import demjanov.av.ru.github.R;
import demjanov.av.ru.github.db.RealmModelUser;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

public class MoreUsersAdapter extends RecyclerView.Adapter<MoreUsersAdapter.MyViewHolder> implements OrderedRealmCollectionChangeListener {
    private RealmResults<RealmModelUser> moreUsers;
    private MyViewHolder mvh;
    private MoreUsersFragment moreUsersFragment;

    interface MoreUsersCall{
        void onCallUser(String userName);
    }

    public MoreUsersAdapter(RealmResults<RealmModelUser> moreUsers, MoreUsersFragment moreUsersFragment) {
        this.moreUsers = moreUsers;
        this.moreUsers.addChangeListener(this);

        this.moreUsersFragment = moreUsersFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_users_recycle_item, parent, false);

        this.mvh = new MyViewHolder(view);

        return this.mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(this.moreUsers.get(position).getLogin());
    }

    @Override
    public int getItemCount() {
        return this.moreUsers.size();
    }

    public int getCurrentPosition(){
        return mvh.getSelectedPosition();
    }


    @Override
    public void onChange(Object o, OrderedCollectionChangeSet changeSet) {
        notifyDataSetChanged();
    }

    private void supportClickItem(){
        this.moreUsersFragment.onCallUser(this.moreUsers.get(getCurrentPosition()).getLogin());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.more_users_item)
        TextView itemTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);

        }

        public void bind(String text){
            itemTextView.setText(text);
        }

        public int getSelectedPosition(){
            return this.getLayoutPosition();
        }

        @OnClick(R.id.more_users_item)
        public void onClickItem(){
            supportClickItem();
        }

    }
}

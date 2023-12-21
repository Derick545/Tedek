package Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tedek.MainActivity;
import com.example.tedek.R;
import com.example.tedek.ViewItems;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import models.categoryModel;

public class categoryAdapter extends FirebaseRecyclerAdapter<models.categoryModel,categoryAdapter.myViewHolder>{
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private int countItems = 0;
    private Context mContext;
    private OnItemClickListener listener,viewListener, deleteListener;

    public categoryAdapter(@NonNull FirebaseRecyclerOptions<categoryModel> options) {
        super(options);
    }
  

    @Override
    protected void onBindViewHolder(@NonNull categoryAdapter.myViewHolder holder, int position, @NonNull categoryModel model) {

        //Declarations
        String id = getRef(position).getKey();

        //Initialising database
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //Create reference
        mRef = FirebaseDatabase.getInstance().getReference().child("Items");

        //load items to holder
        holder.categoryTxt.setText(model.getTitle());
        Picasso.get().load(model.getCategoryImages()).into(holder.categoryImg); //using picasso to get picture


        //Create Value event to count items
        mRef.child(mUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists())
              {
                  //Count items per category
                  countItems = (int) snapshot.getChildrenCount();
                  //Set number of items in collection
                  holder.itemsTxt.setText(Integer.toString(countItems));

                  //calculate items in percentage
                  String totalItems = model.getNumberOfItems();

                  int totalPercentage = countItems / Integer.parseInt(totalItems) *100;
                  //set progress bar
                  holder.progressBar.setMax(100);
                  holder.progressBar.setProgress(totalPercentage);

                  //set percentage to text
                  holder.percentTxt.setText(totalPercentage);

              }
              else
              {
                holder.itemsTxt.setText("0");
                  //set progress bar
                  holder.progressBar.setMax(100);
                  holder.progressBar.setProgress(0);

                  //set percentage to text
                  holder.percentTxt.setText("0");

              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public categoryAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view, parent, false);
        return new myViewHolder(view);
    }


    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        CircleImageView categoryImg;
        TextView categoryTxt, percentTxt, itemsTxt;
        ProgressBar progressBar;
        ImageView menu;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImg = (CircleImageView) itemView.findViewById(R.id.categoryImg);
            categoryTxt = (TextView) itemView.findViewById(R.id.categoryTxt);
            percentTxt = (TextView) itemView.findViewById(R.id.percentTxt);
            itemsTxt = (TextView) itemView.findViewById(R.id.itemsTxt);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            menu = (ImageView) itemView.findViewById(R.id.menuImg);
            menu.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null)
                    {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            //Calling method showPopUpMenu()
            showPopUpMenu(v);
        }
        //Creating method showPopUpMenu()
        private void showPopUpMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.category_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.view)
            {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && viewListener != null)
                        {
                            viewListener.onItemClick(getSnapshots().getSnapshot(position), position);
                        }
                    }
                });
                return true;
            }
            else if(item.getItemId() == R.id.delete)
            {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && viewListener != null)
                        {
                            deleteListener.onItemClick(getSnapshots().getSnapshot(position), position);
                        }
                    }
                });
            }
            return false;
        }
    }
    public  interface OnItemClickListener {
        void onItemClick(DataSnapshot documentSnapShot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void setViewItemClickListener(OnItemClickListener viewListener){this.viewListener = viewListener;}
    public void setDeleteItemClickListener(OnItemClickListener deleteListener){this.deleteListener = deleteListener;}

}

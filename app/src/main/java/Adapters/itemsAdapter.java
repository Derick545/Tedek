package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tedek.R;
import com.example.tedek.editItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import models.itemsModel;

public class itemsAdapter extends FirebaseRecyclerAdapter<models.itemsModel,itemsAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private OnItemClickListener listener,itemListener,viewListener;
    public itemsAdapter(@NonNull FirebaseRecyclerOptions<itemsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull itemsAdapter.myViewHolder holder, int position, @NonNull itemsModel model) {
        holder.itemText.setText(model.getAuthor());

    }

    @NonNull
    @Override
    public itemsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        ImageView itemImage;
        TextView itemText;
        ImageButton imageButton;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImg);
            itemText = (TextView) itemView.findViewById(R.id.itemTxt);
            imageButton = (ImageButton) itemView.findViewById(R.id.menuBtn);
            imageButton.setOnClickListener(this);

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
        }


        @Override
        public void onClick(View v) {
            showPopUpMenu(v);
        }

        private void showPopUpMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.items_popup);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
           if(item.getItemId() == R.id.editMenu)
           {
               itemView.setOnClickListener(new View.OnClickListener(){
                   @Override
                   public void onClick(View v){

                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null)
                    {
                        itemListener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }

                   }
               });
               return true;
           }
           else if(item.getItemId() == R.id.deleteMenu)
           {
               itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       int position = getAdapterPosition();
                       if (position != RecyclerView.NO_POSITION && listener != null)
                       {
                           listener.onItemClick(getSnapshots().getSnapshot(position), position);
                       }
                   }
               });
               return true;
           }

          return false;

        }
    }

    public interface OnItemClickListener{
        void onItemClick(DataSnapshot documentSnapShot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void setTheItemClickListener(OnItemClickListener itemListener){
        this.itemListener = itemListener;
    }
    public void setViewItemClickListener(OnItemClickListener viewListener){
        this.viewListener = viewListener;
    }
}

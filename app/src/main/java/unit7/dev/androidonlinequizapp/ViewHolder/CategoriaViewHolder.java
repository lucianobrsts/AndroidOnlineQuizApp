package unit7.dev.androidonlinequizapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import unit7.dev.androidonlinequizapp.Interface.ItemClickListener;
import unit7.dev.androidonlinequizapp.R;

public class CategoriaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView category_name;
    public ImageView category_image;

    private ItemClickListener itemClickListener;

    public CategoriaViewHolder(View itemView) {
        super(itemView);
        category_image = (ImageView) itemView.findViewById(R.id.category_image);
        category_name = (TextView) itemView.findViewById(R.id.category_name);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

}

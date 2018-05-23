package unit7.dev.androidonlinequizapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import unit7.dev.androidonlinequizapp.Common.Common;
import unit7.dev.androidonlinequizapp.Interface.ItemClickListener;
import unit7.dev.androidonlinequizapp.Model.Categoria;
import unit7.dev.androidonlinequizapp.ViewHolder.CategoriaViewHolder;

public class CategoryFragment extends Fragment {

    View myFragment;

    RecyclerView listCategory;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Categoria, CategoriaViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference categories;

    public static CategoryFragment newInstance(){
        CategoryFragment categoryFragment = new CategoryFragment();
        return categoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_category, container, false);

        listCategory = (RecyclerView) myFragment.findViewById(R.id.listCategory);
        listCategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext());
        listCategory.setLayoutManager(layoutManager);

        loadCategories();

        return myFragment;
    }

    private void loadCategories() {
        adapter = new FirebaseRecyclerAdapter<Categoria, CategoriaViewHolder>(
                Categoria.class,
                R.layout.categoria_layout,
                CategoriaViewHolder.class,
                categories
        ) {
            @Override
            protected void populateViewHolder(CategoriaViewHolder viewHolder, final Categoria model, int position) {
                viewHolder.category_name.setText(model.getName());
                Picasso.with(getActivity())
                        .load(model.getImage())
                        .into(viewHolder.category_image);

                viewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(getActivity(), String.format("%s|%s", adapter.getRef(position).getKey(), model.getName()), Toast.LENGTH_SHORT).show();
                        Intent startGame = new Intent(getActivity(),Start.class);
                        Common.categoryId = adapter.getRef(position).getKey();
                        Common.getCategoryName = model.getName();
                        startActivity(startGame);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        listCategory.setAdapter(adapter);
    }
}
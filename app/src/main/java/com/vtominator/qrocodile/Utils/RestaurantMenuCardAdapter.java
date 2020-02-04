package com.vtominator.qrocodile.Utils;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vtominator.qrocodile.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantMenuCardAdapter extends RecyclerView.Adapter<RestaurantMenuCardAdapter.RestaurantMenuCardItemViewHolder> {
    private static final String TAG = "RestaurantMenuCardAdapt";
    private List<RestaurantMenuCardItem> menuCard;
    private OnItemClickListener mListener;
    public String mainContext;


    public interface OnItemClickListener {
        void onMenuItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class RestaurantMenuCardItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPicture, ivGlutenfree, ivLactosefree;
        private TextView tvPrice, tvName;


        public RestaurantMenuCardItemViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvName = itemView.findViewById(R.id.tvName);

            ivGlutenfree = itemView.findViewById(R.id.ivGlutenfree);
            ivLactosefree = itemView.findViewById(R.id.ivLactosefree);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onMenuItemClick(position);
                    }
                }
            });
        }
    }

    public RestaurantMenuCardAdapter(List<RestaurantMenuCardItem> menuCard, String context) {
        this.mainContext = context;
        this.menuCard = menuCard;
    }

    @NonNull
    @Override
    public RestaurantMenuCardItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_menu, viewGroup, false);
        RestaurantMenuCardItemViewHolder restaurantMenuCardItemViewHolder = new RestaurantMenuCardItemViewHolder(view, mListener);

        return restaurantMenuCardItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantMenuCardItemViewHolder viewHolder, int i) {
        final RestaurantMenuCardItem currentRestaurantMenuCardItem = menuCard.get(i);

        String url = Constants.ROOT_URL + currentRestaurantMenuCardItem.getPicture();
        Picasso.get().load(url).into(viewHolder.ivPicture);


       /* String strJunk = currentRestaurantMenuCardItem.getName();
        try {
            byte[] arrByteForSpanish = strJunk.getBytes("ISO-8859-1");
            viewHolder.tvName.setText(new String(arrByteForSpanish));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        viewHolder.tvName.setText(currentRestaurantMenuCardItem.getName());
        if (mainContext.equals("CartActivity") || mainContext.equals("OrderActivity")) {
            viewHolder.tvPrice.setText(String.valueOf((currentRestaurantMenuCardItem.getPrice() * currentRestaurantMenuCardItem.getPiece())));
        }else
            viewHolder.tvPrice.setText(String.valueOf(currentRestaurantMenuCardItem.getPrice()));

        if (!currentRestaurantMenuCardItem.isGlutenFree()) viewHolder.ivGlutenfree.setVisibility(View.GONE);
        if (!currentRestaurantMenuCardItem.isLactoseFree()) viewHolder.ivLactosefree.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return menuCard.size();
    }


}
package com.example.biketheride.ui.bike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.biketheride.R;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

        private List<Bike> dataSet;


        private final BicisDisponiblesFragment.OnListFragmentInteractionListener mListener;

        public MyItemRecyclerViewAdapter(List<Bike> items, BicisDisponiblesFragment.OnListFragmentInteractionListener listener) {
            mListener = listener;
            this.dataSet = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = dataSet.get(position);
            holder.setOnClickListeners();

            holder.textViewOwner.setText(dataSet.get(position).getOwner());
            holder.textViewCity.setText(dataSet.get(position).getCity());
            holder.textViewLocation.setText(dataSet.get(position).getLocation());
            holder.textViewDescription.setText(dataSet.get(position).getDescription());
            holder.imageViewIcon.setImageBitmap(dataSet.get(position).getImageBitmap());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private final View mView;
            private final TextView textViewOwner;
            private final TextView textViewCity;
            private final TextView textViewLocation;
            private final TextView textViewDescription;

            private final ImageView imageViewIcon;
            private final ImageButton imageButtonEmail;
            private Bike mItem;
            private Context context;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                textViewOwner = view.findViewById(R.id.textViewOwner);
                textViewCity = view.findViewById(R.id.textViewCity);
                imageViewIcon = view.findViewById(R.id.imageViewIcon);
                textViewLocation = view.findViewById(R.id.textViewLocation);
                textViewDescription = view.findViewById(R.id.textViewDescription);
                imageButtonEmail = view.findViewById(R.id.imageButtonEmailCard);
                context = view.getContext();

            }

            void setOnClickListeners() {
                imageButtonEmail.setOnClickListener(this);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + textViewOwner.getText() + "'";
            }


            @Override
            public void onClick(View v) {


                    Toast.makeText(v.getContext(), "Reserva realizada", Toast.LENGTH_LONG).show();
                }
            }
        }



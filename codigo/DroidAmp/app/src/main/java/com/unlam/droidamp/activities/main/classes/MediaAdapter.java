package com.unlam.droidamp.activities.main.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.interfaces.BtnListener;
import com.unlam.droidamp.models.MusicFile;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder>  {

    private ArrayList<MusicFile> mDataset;
    private BtnListener btnListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MediaAdapter(ArrayList<MusicFile> myDataset, BtnListener btnListener) {
        this.mDataset = myDataset;
        this.btnListener = btnListener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtTitle;
        public TextView txtTrackNumber;
        public ImageButton btnPlay;
        public BtnListener btnListener;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTrackNumber = itemView.findViewById(R.id.txtTrack);
            btnPlay = itemView.findViewById(R.id.btnPlay);

            btnPlay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    btnListener.onClick(v, getAdapterPosition());
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.btnListener = this.btnListener;
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.txtTitle.setText(mDataset.get(position).getTitle());
        holder.txtTrackNumber.setText(mDataset.get(position).getTrack().toString() + ".");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

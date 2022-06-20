package wisp.filimoshka.lingvels;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.MyViewHolder> {

    int images[];

    public SliderAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.itemView.setBackgroundResource(images[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (position==0) {
                        Intent intent = new Intent(v.getContext(), Intermediate.class);
                        v.getContext().startActivity(intent);
                    } else if (position==1) {
                        Intent intent = new Intent(v.getContext(), Upper.class);
                        v.getContext().startActivity(intent);
                    } else if (position==2) {
                        Intent intent = new Intent(v.getContext(), Advanced.class);
                        v.getContext().startActivity(intent);
                    }

                } catch (Exception e) {
                  }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View view;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.slider_img);
        }
    }
}

package falcon.assassin.ph.foodblog.view.adapter.post;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import falcon.assassin.ph.foodblog.R;
import falcon.assassin.ph.foodblog.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public List<Post> posts;

    public PostAdapter(List<Post> posts) {

        this.posts = posts;

    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String descData = posts.get(position).getDescription();
        String originalImage = posts.get(position).getOriginalImageUri();
        //Log.d("tag", descData);
        holder.setDescText(descData);
        holder.setOriginalImage(originalImage);
        //Log.d("tag", originalImage);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;

        private ImageView postImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDescText(String desc) {

            descView = mView.findViewById(R.id.mainDesc);
            descView.setText(desc);
        }

        public void setOriginalImage(String image) {

            postImageView = mView.findViewById(R.id.mainImagePost);

            Glide
                    .with(mView.getContext())
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.loadingtransparent).error(R.mipmap.loadingtransparent))
                    .load(image)
                    .into(postImageView);
        }
    }
}

package moe.youranime.main.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.show_view.view.*
import moe.youranime.main.R

class ShowsAdapter(
    private val context: Context,
    private val shows: List<Show>
): RecyclerView.Adapter<ShowsAdapter.ViewHolder>() {
    override fun getItemCount() = shows.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.show_view, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = shows[position]
        holder.textView.text = show.title

        Glide.with(context)
            .load(show.bannerUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    holder.textView.text = String.format("%s (N/A)", holder.textView.text);
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(holder.banner)
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        val banner: ImageView = view.show_view_image_view
        val progressBar: ProgressBar = view.shows_layout_progress_bar
        val textView: TextView = view.shows_layout_title

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.i("ShowsAdapter","Clicked on show!")
        }
    }
}
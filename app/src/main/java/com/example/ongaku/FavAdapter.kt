package com.example.ongaku

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ongaku.databinding.FavouriteviewBinding

class FavAdapter(private val context: Context, private var musiclist:ArrayList<Music>):RecyclerView.Adapter<FavAdapter.holder>() {

    class holder(binding: FavouriteviewBinding):RecyclerView.ViewHolder(binding.root)
    {
        val img = binding.songimgFV
        val sngnme = binding.songnameFV
        val root = binding.root
        val favbtn = binding.favbtnFV
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):holder {
        return holder(FavouriteviewBinding.inflate(LayoutInflater.from(context), parent , false))
    }

    override fun onBindViewHolder(holder:holder, position: Int) {
      holder.sngnme.text = musiclist[position].title
        holder.sngnme.isSelected = true
        Glide.with(context)
            .load(musiclist[position].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
            .into(holder.img)

            holder.favbtn.setImageResource(R.drawable.ic_baseline_favorite_24)

        holder.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavAdapter")
                ContextCompat.startActivity(context, intent, null)

        }
        holder.favbtn.setOnClickListener {
            holder.favbtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            PlayerActivity.isFav = false
            Favactivity.favSongs.removeAt(position)

            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
       return musiclist.size
    }
}
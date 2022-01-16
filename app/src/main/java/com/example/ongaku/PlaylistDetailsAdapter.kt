package com.example.ongaku

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ongaku.databinding.PlaylistdetailsviewBinding


class PlaylistDetailsAdapter(private val context: Context, private var musiclist:ArrayList<Music>) : RecyclerView.Adapter<PlaylistDetailsAdapter.Holder>() {

    class Holder(binding:PlaylistdetailsviewBinding):RecyclerView.ViewHolder(binding.root){

        var title = binding.songnamePD
        val album = binding.albumPD
        val image = binding.imagePD
        val duration = binding.songdurationPD
        val root = binding.root
        val del = binding.delPD
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(PlaylistdetailsviewBinding.inflate(LayoutInflater.from(context), parent , false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.text = musiclist[position].title
        holder.album.text = musiclist[position].album
        holder.duration.text = formatDuration(musiclist[position].duration)
        Glide.with(context)
            .load(musiclist[position].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
            .into(holder.image)

        holder.root.setOnClickListener {
                when (musiclist[position].id) {
                    PlayerActivity.currentsongid -> sendIntent(
                        ref = "Now Playing",
                        pos = PlayerActivity.songPosition
                    )
                    else -> sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }


        }
        holder.del.setOnClickListener {
            Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist.removeAt(position)
            updatePlaylistDetails()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
      return musiclist.size
    }

    fun updatePlaylist(){
        musiclist = ArrayList()
        musiclist =  Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist
        notifyDataSetChanged()
    }
        private fun sendIntent(ref:String , pos:Int)
        {
            val intent = Intent(context , PlayerActivity::class.java)
            intent.putExtra("index" , pos)
            intent.putExtra("class" , ref)

            ContextCompat.startActivity(context , intent , null)
        }
}



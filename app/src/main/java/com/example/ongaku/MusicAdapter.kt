package com.example.ongaku

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ongaku.databinding.MusicViewBinding


class MusicAdapter(private val context: Context, private var musiclist:ArrayList<Music>, private var playlistDetails: Boolean = false , private var selectionActivity: Boolean = false) : RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    var playlistSongs : ArrayList<Music> = ArrayList()
    class MyHolder(binding:MusicViewBinding):RecyclerView.ViewHolder(binding.root){

         var title = binding.songnamemv
         val album = binding.albummv
         val image = binding.imageMV
         val duration = binding.songduration
         val root = binding.root

     }



     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
       return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent , false))
     }

     override fun onBindViewHolder(holder: MyHolder, position: Int) {
          holder.title.text = musiclist[position].title
         holder.album.text = musiclist[position].album
         holder.duration.text = formatDuration(musiclist[position].duration)
         Glide.with(context)
             .load(musiclist[position].arturi)
             .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
             .into(holder.image)

         when{
             
             selectionActivity ->{
                 holder.root.setOnClickListener {
                     playlistSongs.add(musiclist[position])
                     holder.root.setBackgroundColor(ContextCompat.getColor(context , R.color.cool_pink))

                 }
             }
             else->{
                 holder.root.setOnClickListener {
                     when{
                         MainActivity.search->sendIntent(ref ="MusicAdapterSearch" , pos = position)
                         musiclist[position].id == PlayerActivity.currentsongid ->sendIntent(ref = "Now Playing" , pos = PlayerActivity.songPosition)
                         else->sendIntent(ref ="MusicAdapter", pos = position)
                     }

                 }
             }

         }

     }



    override fun getItemCount(): Int {
         return musiclist.size
     }

     fun updateList(serachList:ArrayList<Music>)
     {
         musiclist = ArrayList()
         musiclist.addAll(serachList)
         notifyDataSetChanged()
     }

    private fun sendIntent(ref:String , pos:Int)
    {
        val intent = Intent(context , PlayerActivity::class.java)
        intent.putExtra("index" , pos)
        intent.putExtra("class" , ref)

        ContextCompat.startActivity(context , intent , null)
    }
     fun addsong(song:ArrayList<Music>):Boolean{
        Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist.addAll(song)

        return true
    }
    fun updatePlaylist(){
       musiclist = ArrayList()
        musiclist =  Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist
        notifyDataSetChanged()
    }

}


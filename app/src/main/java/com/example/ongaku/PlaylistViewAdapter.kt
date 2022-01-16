package com.example.ongaku

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ongaku.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder



class PlaylistViewAdapter(private val context: Context, private var playlist: ArrayList<Playlist>): RecyclerView.Adapter<PlaylistViewAdapter.holder>()
{

    class holder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root)
    {
        val root = binding.root
        val txt  = binding.playlistnamePV
        val img = binding.Playlistimg
        val delbtn = binding.playlistdelbtn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):holder {
        return holder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent , false))
    }

    override fun onBindViewHolder(holder:holder, position: Int) {
        PlaylistDetails()
        holder.txt.text = playlist[position].name
        holder.txt.isSelected = true


        holder.delbtn.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Del")
                .setMessage("Do you want to delete the playlist?")
                .setPositiveButton("Yes")
                { dialog, _ ->
                    Playlistactivity.playlists.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()

                } //.setPositiveButton(resources.getString(R.string.accept)) { dialog, which -> // Respond to positive button press }
                .setNegativeButton("No"){dialog , _->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.DKGRAY)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.DKGRAY)
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.CYAN)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.CYAN)
        }

        holder.root.setOnClickListener {
            val intent = Intent(context , PlaylistDetails::class.java)
            intent.putExtra("index" , position)
            ContextCompat.startActivity(context , intent , null)
        }

        if(Playlistactivity.playlists.ref[position].playlist.size>0){
            Glide.with(context)
                .load(Playlistactivity.playlists.ref[position].playlist[0].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
                .into(holder.img)
        }



        }

    override fun getItemCount(): Int {
        return playlist.size
    }

    fun refreshPlaylist()
    {
        playlist = ArrayList()
        playlist.addAll(Playlistactivity.playlists.ref)
        notifyDataSetChanged()
    }


}
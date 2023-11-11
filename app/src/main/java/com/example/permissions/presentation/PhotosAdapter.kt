package com.example.permissions.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.permissions.databinding.PhotoItemBinding
import com.example.permissions.entity.PhotoInRepo
import javax.inject.Inject

class PhotosAdapter : ListAdapter<PhotoInRepo, PhotoViewHolder>(DiffutilCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
        with(holder.binding) {
            Glide.with(itemLayout.context)
                .load(photoItem.uri)
                .into(image)
        }
    }
}

class DiffutilCallback: DiffUtil.ItemCallback<PhotoInRepo>() {
    override fun areItemsTheSame(oldItem: PhotoInRepo, newItem: PhotoInRepo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PhotoInRepo, newItem: PhotoInRepo): Boolean {
        return oldItem.uri == newItem.uri
    }
}

class PhotoViewHolder @Inject constructor
    (val binding: PhotoItemBinding) :
    RecyclerView.ViewHolder(binding.root)

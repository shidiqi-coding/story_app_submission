package com.dicoding.storyapp.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemListStoryBinding

class ListStoryAdapter(
     private val onItemClicked: (String, ImageView, TextView, TextView) -> Unit
) : ListAdapter<ListStoryItem, ListStoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
          val binding = ItemListStoryBinding.inflate(
               LayoutInflater.from(parent.context), parent, false
          )
          return StoryViewHolder(binding)
     }

     override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
          val story = getItem(position)
          holder.bind(story)

          story.id?.let { id ->
               holder.binding.imgStory.transitionName = "image_detail_$id"
               holder.binding.tvNameStory.transitionName = "title_$id"
               holder.binding.tvSummaryStory.transitionName = "description_$id"

               holder.itemView.setOnClickListener {
                    onItemClicked(
                         id,
                         holder.binding.imgStory,
                         holder.binding.tvNameStory,
                         holder.binding.tvSummaryStory
                    )
               }
          }
     }

     class StoryViewHolder(val binding: ItemListStoryBinding) :
          RecyclerView.ViewHolder(binding.root) {

          fun bind(story: ListStoryItem) {
               binding.tvNameStory.text = story.name
               binding.tvSummaryStory.text = story.description
               Glide.with(binding.root.context)
                    .load(story.photoUrl)
                    .into(binding.imgStory)
          }
     }

     class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
          override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
               return oldItem.id == newItem.id
          }

          override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
               return oldItem == newItem
          }
     }
}

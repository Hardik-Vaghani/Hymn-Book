package com.hardik.hymnbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hardik.hymnbook.R
import com.hardik.hymnbook.common.Constants.BASE_TAG
import com.hardik.hymnbook.data.dto.HymnBookIndexListItem
import com.hardik.hymnbook.databinding.ItemIndexPreviewBinding

class IndexAdapter: RecyclerView.Adapter<IndexAdapter.IndexViewHolder>(), Filterable {
    private val TAG = BASE_TAG + IndexAdapter::class.java
    inner class IndexViewHolder(val binding: ItemIndexPreviewBinding):RecyclerView.ViewHolder(binding.root)

    private val differCallback = object: DiffUtil.ItemCallback<HymnBookIndexListItem>(){
        override fun areItemsTheSame(
            oldItem: HymnBookIndexListItem,
            newItem: HymnBookIndexListItem
        ): Boolean {
            return oldItem.file == newItem.file && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: HymnBookIndexListItem,
            newItem: HymnBookIndexListItem
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this@IndexAdapter,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndexViewHolder {
        return IndexViewHolder(ItemIndexPreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: IndexViewHolder, position: Int) {
        val indexItem = differ.currentList[position]

        val prefs = PreferenceManager.getDefaultSharedPreferences(holder.itemView.context.applicationContext)
        val language = prefs.getString("index_item_language", null)
        val indexItemValues: Array<String> = holder.itemView.context.resources.getStringArray(R.array.index_item_language_values)

        holder.binding.itemIndexPreviewTvTitle.text = if(language == indexItemValues[0]) indexItem.title else indexItem.title_hindi
//        holder.binding.itemIndexPreviewTvTitle.text = indexItem.title
        holder.binding.itemIndexPreviewTvTitle.setHorizontallyScrolling(true)
        holder.binding.itemIndexPreviewTvTitle.isSelected = true

        holder.itemView.setOnClickListener {
//            onItemClickListener?.let { it(HymnBookIndexListItem(title = indexItem.title, title_hindi = indexItem.title_hindi, file = indexItem.file,)) }
            onItemClickListener?.invoke(indexItem)
        }

    }

    private var onItemClickListener : ((HymnBookIndexListItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (HymnBookIndexListItem) -> Unit){
        onItemClickListener = listener
    }

    // Function to clear selection
    fun clearSelection() {
        val currentList = differ.currentList
        currentList.forEach { it.isSelected = false }
        setOriginalList(currentList)
    }

    private var originalList: List<HymnBookIndexListItem> = emptyList()

    // Function to set the original list
    fun setOriginalList(list: List<HymnBookIndexListItem>) {
        originalList = list
//        differ.submitList(list)
        differ.submitList(originalList.map { it.copy(isSelected = it.isSelected) })// Restore isSelected state from originalList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.title.contains(constraint, true) ||
                        it.title_hindi.contains(constraint, true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                differ.submitList(results?.values as List<HymnBookIndexListItem>?)
            }
        }
    }

}
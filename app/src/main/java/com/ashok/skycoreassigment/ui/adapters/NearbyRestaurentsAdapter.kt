package com.ashok.skycoreassigment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ashok.skycoreassigment.R
import com.ashok.skycoreassigment.databinding.RestaurantsRowBinding
import com.ashok.skycoreassigment.model.Businesses
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class NearbyRestaurentsAdapter : PagingDataAdapter<Businesses, RecyclerView.ViewHolder>(
    BusinessModelComparator
)  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RestaurantsRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        val restaurentList: Businesses = getItem(position)!!

        val viewHolder = holder as MyViewHolder

        viewHolder.bindCollectionsData(restaurentList)
    }

    inner class MyViewHolder(private val binding: RestaurantsRowBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindCollectionsData(restaurents: Businesses) {
            binding.apply {
                txtResName.text = restaurents.name
                txtAddress.text = restaurents.location.address1
                txtRating.text = restaurents.rating


                try {
                    val df = DecimalFormat("#")
                    val distance_in_m = df.format(restaurents.distance).toString()
                    txtDistance.text = distance_in_m+" Meters"
                }catch (e:Exception)
                {

                    txtDistance.text = "No Data"
                }


                val status = if (restaurents.is_closed) "Closed" else "Open"

                txtOpenStatus.text = status

                Glide.with(itemView)
                    .load(restaurents.image_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(imgRestaurent)
                root.setOnClickListener {
                }
            }
        }
    }
    companion object {
        private val BusinessModelComparator = object : DiffUtil.ItemCallback<Businesses>() {
            override fun areItemsTheSame(oldItem: Businesses, newItem: Businesses): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Businesses, newItem: Businesses): Boolean =
                oldItem == newItem
        }
    }

}
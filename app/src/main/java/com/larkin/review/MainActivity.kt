package com.larkin.review

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.larkin.review.databinding.ActivityMainBinding
import com.larkin.review.databinding.ItemPointBinding
import com.larkin.review.handler.HandlerActivity

class MainActivity : AppCompatActivity() {
    val mPointNames: ArrayList<PointEntity> = arrayListOf<PointEntity>().apply {
        add(PointEntity("AMS", null))
        add(PointEntity("Binder", null))
        add(PointEntity("GC", null))
        add(PointEntity("Glide", null))
        add(PointEntity("Handler", HandlerActivity::class.java))
        add(PointEntity("Https", null))
        add(PointEntity("IM", null))
        add(PointEntity("OkHttp", null))
        add(PointEntity("Render", null))
        add(PointEntity("ThreadPool", null))
        add(PointEntity("Performance", null))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.pointAdapter = PointAdapter()
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(typedValue.data))
    }

    inner class PointAdapter : RecyclerView.Adapter<PointAdapter.PointVH>() {
        inner class PointVH(private val binding: ItemPointBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bindData(pointName: String) {
                binding.pointName = pointName
            }

            fun bindListener(onClickListener: View.OnClickListener) {
                binding.root.setOnClickListener(onClickListener)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointVH {
            val inflater = LayoutInflater.from(parent.context)
            val itemPointBinding = DataBindingUtil.inflate<ItemPointBinding>(
                inflater,
                R.layout.item_point,
                parent,
                false
            )
            return PointVH(itemPointBinding)
        }

        override fun onBindViewHolder(holder: PointVH, position: Int) {
            holder.bindData(mPointNames[position].name)
            holder.bindListener {
                mPointNames[position].cls?.run {
                    startActivity(Intent(this@MainActivity, this))
                }
            }
        }

        override fun getItemCount(): Int {
            return mPointNames.size
        }
    }

    data class PointEntity(val name: String, val cls: Class<out Activity>?)
}
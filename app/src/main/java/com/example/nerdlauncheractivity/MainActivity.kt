package com.example.nerdlauncheractivity

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.content.res.ComplexColorCompat.inflate
import androidx.core.graphics.drawable.DrawableCompat.inflate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.zip.Inflater

private const val TAG = "NerdLauncherActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        setupAdapter()
    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val activities = packageManager.queryIntentActivities(startupIntent, 0)
        activities.sortWith(Comparator { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                    a.loadLabel(packageManager).toString(),
                    b.loadLabel(packageManager).toString()
            )
        })
        Log.i(TAG, "Found ${activities.size} activities")
        recyclerView.adapter = ActivityAdapter(activities)
    }

    private class ActivityHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener {
        private val nameTextView = itemView.findViewById<TextView>(R.id.text_view)
        private val iconImageView = itemView.findViewById<ImageView>(R.id.icon_view)
        init {
            nameTextView.setOnClickListener(this)
        }

        private lateinit var resolveInfo: ResolveInfo
        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            iconImageView.setImageDrawable(resolveInfo.loadIcon(packageManager))
            nameTextView.text = appName
        }

        override fun onClick(view: View) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName,
                        activityInfo.name)
            }
            val context = view.context
            context.startActivity(intent)
        }
    }
    private class ActivityAdapter(val activities: List<ResolveInfo>) :
            RecyclerView.Adapter<ActivityHolder>() {
        override fun onCreateViewHolder(container: ViewGroup, viewType: Int):
                ActivityHolder {
//            val layoutInflater = LayoutInflater.from(container.context)
//            val view = layoutInflater
//                    .inflate(android.R.layout.simple_list_item_1, container, false)
            val view= LayoutInflater.from(container.context).inflate(R.layout.list_item, container, false)
            return ActivityHolder(view)
        }
        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }
        override fun getItemCount(): Int {
            return activities.size
        }
    }


}
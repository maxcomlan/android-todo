package models

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.maxbit.todo.R

interface TaskStatusChangeListener {
    fun onSetOngoing(task: Task)
    fun onSetCompleted(task: Task)
}

class TaskAdapter(private val context: Context, private val items: List<Task>): RecyclerView.Adapter<TaskAdapter.ViewHolder>(){

    public lateinit var  statusChangeListener: TaskStatusChangeListener

    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.task_entry_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val task = items[position]
        holder.titleTextView.text = shrinkCardText(task.title)
        holder.descriptionTextView.text = shrinkCardText(task.description)
        val iconView = holder.itemView.findViewById<ImageView>(R.id.task_state_icon)

        if(task.isPending()){
            iconView.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_baseline_radio_button_unchecked_24))
        }
        else if(task.isOngoing()){
            iconView.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_baseline_more_horiz_24))
        }
        else if(task.isCompleted()){
            iconView.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_baseline_done_24))
        }

        when(task.isCompleted()){
            false -> holder.itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
                run {
                    MenuInflater(this.context).inflate(R.menu.task_action_menu, menu)

                    menu.findItem(R.id.task_go_ongoing).setOnMenuItemClickListener { it ->
                        run {
                            statusChangeListener.onSetOngoing(task)
                            return@run true
                        }
                    }

                    menu.findItem(R.id.task_go_completed).setOnMenuItemClickListener { it ->
                        run {
                            statusChangeListener.onSetCompleted(task)
                            return@run true
                        }
                    }

                    if(task.isOngoing()){
                        menu.removeItem(R.id.task_go_ongoing)
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
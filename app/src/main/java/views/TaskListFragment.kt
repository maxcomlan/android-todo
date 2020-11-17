package views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.maxbit.todo.R
import io.realm.Realm
import io.realm.kotlin.where
import models.Task
import models.TaskAdapter
import models.TaskStatusChangeListener
import java.util.*
import kotlin.properties.Delegates

interface OnTaskActionRequested {
    fun onCreateTask()
}

class TaskListFragment: Fragment(), TaskStatusChangeListener {
    public val NAME = "TaskListFragment"
    private lateinit var realm: Realm
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var  recyclerView: RecyclerView

    public lateinit var onTaskActionRequested: OnTaskActionRequested

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tasklist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_create_task)
        fab.setOnClickListener { _ ->
            run {
                this.onTaskActionRequested.onCreateTask()
            }
        }
        setupRecycler()
    }

    override fun onStart() {
        super.onStart()
        fetchTasks()
    }

    private fun fetchTasks(){
        val tasks = realm.where<Task>().findAll()
            .apply {
                addChangeListener { tasks ->
                    run {
                        taskAdapter = TaskAdapter(context!!, tasks).apply {
                            statusChangeListener = this@TaskListFragment
                        }
                        setupAdapter()
                    }
                }
            }

        taskAdapter = TaskAdapter(context!!, tasks).apply {
            statusChangeListener = this@TaskListFragment
        }

        setupAdapter()
    }

    private fun setupRecycler(){
        recyclerView = view?.findViewById<RecyclerView>(R.id.task_recycler)!!
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun setupAdapter(){
        recyclerView.adapter = taskAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
    }

    override fun onSetOngoing(task: Task) {
        val clone = task.carbonCopy()
        clone.setOngoing()
        val writeRealm = Realm.getDefaultInstance()
        writeRealm.executeTransactionAsync({
            realm -> run {
                realm.copyToRealmOrUpdate(clone)
            }
            }, {
                Toast.makeText(this.context, "Tache desormais en cours", Toast.LENGTH_LONG).show()
            }, {
                err -> run {
                Toast.makeText(this.context, err.message , Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onSetCompleted(task: Task) {
        val clone = task.carbonCopy()
        clone.setCompleted()
        val writeRealm = Realm.getDefaultInstance()
        writeRealm.executeTransactionAsync({
            realm -> run {
                realm.copyToRealmOrUpdate(clone)
            }
            }, {
                Toast.makeText(this.context, "Tache complétée", Toast.LENGTH_LONG).show()
            }, {
                err -> run {
                Toast.makeText(this.context, err.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}
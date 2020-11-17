package views

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.maxbit.todo.R
import io.realm.Realm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.Task

class CreateTaskFragment: Fragment(){
    public val NAME = "CreateTaskFragment"

    private var task: Task = Task()
    private lateinit var titleInput: TextInputEditText
    private lateinit var descInput: TextInputEditText
    private lateinit var btnExec: MaterialButton

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_task_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleInput = view.findViewById(R.id.task_title_input)
        descInput = view.findViewById(R.id.task_desc_input)
        btnExec = view.findViewById(R.id.action_create_task)

        btnExec.setOnClickListener{_ ->
            run {
                val task = Task()
                    .apply {
                        title = titleInput.text.toString()
                        description = descInput.text.toString()
                        setPending()
                    }
                createTask(task)
            }
        }
    }

    private fun createTask(task: Task){
        realm.executeTransactionAsync(
                { realm ->
                    run {
                        println("Task id is ${task.id}")
                        println("Task title is ${task.title}")
                        realm.copyToRealm(task)
                        Log.d(NAME, "Inserted task")
                    }
                },
                {
                    Toast.makeText(
                        context,
                        getString(R.string.create_task_succeeded),
                        Toast.LENGTH_LONG
                    ).show()
                    titleInput.setText("")
                    descInput.setText("")
                },
                {
                    _ -> run {
                    Toast.makeText(
                        context,
                        getString(R.string.create_task_errored),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}
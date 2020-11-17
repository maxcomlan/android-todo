package io.maxbit.todo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import io.realm.Realm
import io.realm.RealmConfiguration
import views.CreateTaskFragment
import views.OnTaskActionRequested
import views.TaskListFragment

class MainActivity : AppCompatActivity(), OnTaskActionRequested {

    private val taskListFragment = TaskListFragment()
    private val createTaskFragment = CreateTaskFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        Realm.init(this)
        setupRealmConfigs()
    }

    override fun onStart() {
        super.onStart()
        setupFragments()
        showTaskList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_clear_list -> {
                clearAll()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = getString(R.string.toolbar_task_title)
        setSupportActionBar(toolbar)
    }

    private fun setupFragments(){
        if (supportFragmentManager.fragments.size == 0){
            taskListFragment.onTaskActionRequested = this
            val transaction = supportFragmentManager.beginTransaction()
            transaction.apply {
                add(R.id.fragment_frame, taskListFragment, taskListFragment.NAME)
                add(R.id.fragment_frame, createTaskFragment, createTaskFragment.NAME)
                addToBackStack(createTaskFragment.NAME)
                commit()
            }
        }
    }

    override fun onBackPressed() {
        if( supportFragmentManager.primaryNavigationFragment === createTaskFragment) {
            showTaskList()
        }
        else{
            super.onBackPressed()
        }
    }

    private fun showTaskList(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            hide(createTaskFragment)
            addToBackStack(createTaskFragment.NAME)
            show(taskListFragment)
            commit()
        }
    }

    private fun goToCreate(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            hide(taskListFragment)
            addToBackStack(taskListFragment.NAME)
            show(createTaskFragment)
            commit()
        }
    }

    private fun clearAll(){
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync({
            realm -> run {
                realm.deleteAll()
            }
        }, {
            Toast.makeText(applicationContext, "Les taches ont été supprimées", Toast.LENGTH_LONG).show()
        }, {
            err -> run{
            Toast.makeText(applicationContext, err.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupRealmConfigs(){
        val defaultConf = RealmConfiguration
            .Builder()
            .name("default.realm")
            .build()

        Realm.setDefaultConfiguration(defaultConf)
        print("Default path is ${Realm.getDefaultInstance().path}")
    }

    override fun onCreateTask() {
        goToCreate()
    }
}
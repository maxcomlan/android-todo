package models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.time.Instant
import java.util.*

open class Task: RealmObject () {

    @PrimaryKey
    var id: String = ""
    var title: String = ""
    var description : String = ""
    var createdAt: Long = 0L
    var status: String = "pending"

    init {
        this.id = UUID.randomUUID().toString()
        this.createdAt = Instant.now().toEpochMilli()
    }

    fun isPending(): Boolean{
        return status == "pending"
    }

    fun isCompleted(): Boolean{
        return status == "completed"
    }

    fun isOngoing(): Boolean{
        return status == "ongoing"
    }

    fun setPending(){
        status = "pending"
    }

    fun setOngoing(){
        status = "ongoing"
    }

    fun setCompleted(){
        status = "completed"
    }

    fun carbonCopy(): Task{
        val clone = Task()
        clone.id = this.id
        clone.title = this.title
        clone.description = this.description
        clone.createdAt = this.createdAt
        clone.status = this.status
        return clone
    }
}
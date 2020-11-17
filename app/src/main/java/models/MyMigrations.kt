package models

import io.realm.DynamicRealm
import io.realm.RealmMigration


class MyMigrations: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val oldV = oldVersion
        val schema = realm.schema
        if(oldV == 0L){
            /// V1 added status on Tasks plus a primary key
            schema.get("Task").apply {
                this?.addField("status", String::class.java)
                this?.addPrimaryKey("id")
            }

            //oldV++
        }
    }

}
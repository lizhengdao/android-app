package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ConnectionDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(settings: ConnectionSettingsEntity): Long

  @Delete
  fun delete(settings: ConnectionSettingsEntity)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(settings: ConnectionSettingsEntity)

  @Query("select * from settings where id = :defaultId")
  fun findById(defaultId: Long): ConnectionSettingsEntity?

  @Query("select * from settings")
  fun getAll(): LiveData<List<ConnectionSettingsEntity>>

  @Query("select count(*) from settings")
  fun count(): Long

  @Query("select * from settings limit 1")
  fun first(): ConnectionSettingsEntity?

  @Query("select * from settings order by id desc limit 1")
  fun last(): ConnectionSettingsEntity?

  @Query("select * from settings where id < :id order by id desc limit 1")
  fun getPrevious(id: Long): ConnectionSettingsEntity?

  @Query("select id from settings where address = :address and port = :port")
  fun findId(address: String, port: Int): Long?

  @Query("select * from settings where id = :id")
  fun getById(id: Long): LiveData<ConnectionSettingsEntity>
}
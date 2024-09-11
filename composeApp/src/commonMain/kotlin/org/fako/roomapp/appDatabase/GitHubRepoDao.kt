package org.fako.roomapp.appDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubRepoDao {

    @Insert
    suspend fun insert(item: GithubRepoEntity)

    @Delete
    suspend fun delete(item:GithubRepoEntity)

    @Update
    suspend fun update(item:GithubRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<GithubRepoEntity>)

    @Query("SELECT * FROM GithubRepoEntity")
    fun getAllAsFlow(): Flow<List<GithubRepoEntity>>


    @Query("SELECT * FROM GithubRepoEntity")
    suspend fun getAll(): List<GithubRepoEntity>

    @Query("SELECT count(*) FROM GithubRepoEntity")
    suspend fun count(): Int
}

@Entity
data class GithubRepoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val description: String,
)
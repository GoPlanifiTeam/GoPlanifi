package com.example.goplanify

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.goplanify.data.local.AppDatabase
import com.example.goplanify.data.local.dao.UserDao
import com.example.goplanify.data.local.entity.UserEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetUserById() = runBlocking {
        val user = UserEntity(
            userId = "user123",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            imageURL = ""
        )
        userDao.insertUser(user)
        val result = userDao.getUserById("user123")
        assertEquals("test@example.com", result?.email)
    }
}
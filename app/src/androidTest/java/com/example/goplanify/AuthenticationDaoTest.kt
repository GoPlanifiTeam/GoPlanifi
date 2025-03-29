package com.example.goplanify

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.goplanify.data.local.AppDatabase
import com.example.goplanify.data.local.dao.AuthenticationDao
import com.example.goplanify.data.local.entity.AuthenticationEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthenticationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AuthenticationDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.authenticationDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndFetchAuthentication() = runBlocking {
        val auth = AuthenticationEntity("user123", 0)
        dao.insertOrUpdate(auth)
        val result = dao.getByUserId("user123")
        assertNotNull(result)
        assertEquals(0, result?.loginErrors)
    }
}

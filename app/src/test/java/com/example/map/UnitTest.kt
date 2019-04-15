package com.example.map

import com.example.map.utils.MapException
import com.example.map.utils.UsersParser
import org.junit.Assert.assertEquals
import org.junit.Test


class UnitTest {

    @Test fun usersParserTest() {
        val usersString = "11,Name1,http://someurl.jpg,56.9495677035,24.1064071655;12,Name2,http://someurl.jpg,56.9495677035,24.1064071655;"
        val users = UsersParser().parse(usersString)
        assertEquals(users.size, 2)
        assertEquals(users[0].id, 11)
        assertEquals(users[0].name, "Name1")
        assertEquals(users[1].imageUrl, "http://someurl.jpg")
        assertEquals(users[0].lon, 56.9495677035, 0.0001)
    }

    @Test fun userParserFail() {
        try {
            val usersString = "random string"
            UsersParser().parse(usersString)
            assert(false)
        } catch (e: MapException) {
            assert(true)
        }
    }

}

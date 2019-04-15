package com.example.map.utils

import com.example.map.models.User

class UsersParser {
    fun parse(usersString: String): List<User> {
        var users = ArrayList<User>()
        val usersStringList =  usersString.split(";").toList();
        for (userString in usersStringList) {
            users.add(parseUser(userString))
        }

        return users;
    }

    fun parseUser(userString: String): User {
        val u = userString.split(",")
        if (u.size == 5) {
            val id = u.get(0).toInt()
            val username = u.get(1)
            val image = u.get(2)
            val lon = u.get(3).toDouble()
            val lat = u.get(4).toDouble()
            return User(id, username, image, lon, lat)
        }
        //TODO MB custom exception
        throw RuntimeException("Server Error")
    }
}
package com.example.map.utils

import com.example.map.models.User

class UsersParser {
    fun parse(usersString: String): List<User> {
        var users = ArrayList<User>()
        val usersStringList =  usersString.split(";").toList();
        for (userString in usersStringList) {
            if (userString.isEmpty()) continue
            users.add(parseUser(userString))
        }

        return users;
    }

    /**
     * userString example: 101,Jānis Bērziņš,http://someurl.jpg,56.9495677035,24.1064071655
     */
    fun parseUser(userString: String): User {
        val u = userString.split(",")
        if (u.size == 5) {
            val id = u.get(0).toInt()
            val username = u.get(1)
            val image = u.get(2)
            val lat = u.get(3).toDouble()
            val lon = u.get(4).toDouble()
            return User(id, username, image, lon, lat)
        }
        throw MapException("Server Error")
    }
}
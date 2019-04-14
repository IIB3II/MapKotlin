package com.example.map.presenter

import com.example.map.consts.Command
import com.example.map.consts.Config
import com.example.map.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.Socket

class MapsPresenter() {
    private var clientSocket: Socket? = null
    private var outToServer: DataOutputStream? = null
    private var inFromServer: BufferedReader? = null

    init {
        GlobalScope.launch(Dispatchers.IO) {
            clientSocket = Socket(Config.SERVER, Config.PORT);
            outToServer = DataOutputStream(clientSocket!!.getOutputStream())
            inFromServer = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
        }
    }

    private suspend fun authorizeCommand() : String {
        return withContext(Dispatchers.IO) {
            outToServer!!.writeBytes("${Command.AUTHORIZE} ${Config.EMAIL}\n")
            inFromServer!!.readLine()
        }
    }

    fun closeConnection() {
        clientSocket!!.close()
    }

    suspend fun authorize() : List<User> {
        var users = ArrayList<User>()
        val response = authorizeCommand()
        if(response.startsWith(Command.USERLIST)) {
            val usersString = response.replace(Command.USERLIST, "").trim();
            users.addAll(parseUsers(usersString))
        }

        return users;
    }

    fun parseUsers(usersString: String): List<User> {
        var users = ArrayList<User>()
        val usersStringList =  usersString.split(";").toList();
        for (userString in usersStringList) {
            users.add(parseUser(userString))
        }


        return users;
    }

    /**
     * 101,Jānis Bērziņš,http://someurl.jpg,56.9495677035,24.1064071655
     */
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
package com.example.map.presenter

import android.text.TextUtils
import androidx.annotation.Nullable
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
import java.net.SocketException

class MapsPresenter(server: String, port: Int) {
    private var mSocket: Socket? = null
    private var mOut: DataOutputStream? = null
    private var mIn: BufferedReader? = null
    private var mUsers = ArrayList<User>()
    private var mIsAuthorized = false;

    init {
        GlobalScope.launch(Dispatchers.IO) {
            mSocket = Socket(server, port);
            mOut = DataOutputStream(mSocket!!.getOutputStream())
            mIn = BufferedReader(InputStreamReader(mSocket!!.getInputStream()))
        }
    }

    private suspend fun authorizeCommand(): String {
        return withContext(Dispatchers.IO) {
            mOut!!.writeBytes("${Command.AUTHORIZE} ${Config.EMAIL}\n")
            mIn!!.readLine()
        }
    }

    fun closeConnection() {
        if (mSocket != null) mSocket!!.close()
        mIsAuthorized = false
    }

    suspend fun authorize(): List<User> {
        var response = "";
        try {
            response = authorizeCommand()
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        if(response.startsWith(Command.USERLIST)) {
            val usersString = response.replace(Command.USERLIST, "").trim()
            mUsers.addAll(parseUsers(usersString))
            mIsAuthorized = true;
        }
        return mUsers;
    }

    @Nullable fun update(): User? {
        if (!mIsAuthorized  || mSocket!!.isClosed) return null
        val response: String
        try {
            response = mIn!!.readLine()
        } catch (e: SocketException) {
            e.printStackTrace()
            return null;
        }
        println(response)
        if(response.startsWith(Command.UPDATE)) {
            val userUpdatedString = response.replace(Command.UPDATE, "").trim()
            val u = userUpdatedString.split(",")
            if (u.size == 3) {
                val id = u.get(0).toInt()
                val lat = u.get(1).toDouble()
                val lon = u.get(2).toDouble()
                val user = getUser(id);
                if (user != null) {
                    user.lat = lat
                    user.lon = lon
                    return user;
                }
            }
        }
        return null;
    }

    @Nullable private fun getUser(id: Int): User? {
        for (user in mUsers) {
            if (user.id == id) return user
        }
        return null;
    }

    private fun parseUsers(usersString: String): List<User> {
        val users = ArrayList<User>()
        val usersStringList =  usersString.split(";").toList()
        for (userString in usersStringList) {
            if (TextUtils.isEmpty(userString)) continue
            users.add(parseUser(userString))
        }

        return users;
    }

    /**
     * 101,Jānis Bērziņš,http://someurl.jpg,56.9495677035,24.1064071655
     */
    private fun parseUser(userString: String): User {
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
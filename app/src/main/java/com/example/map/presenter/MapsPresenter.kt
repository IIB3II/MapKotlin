package com.example.map.presenter

import androidx.annotation.Nullable
import com.example.map.consts.Command
import com.example.map.consts.Config
import com.example.map.models.User
import com.example.map.utils.MapAction
import com.example.map.utils.MapException
import com.example.map.utils.UsersParser
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
    private var mIsAuthorized = false

    private var mOnError: MapAction<String>? = null

    init {
        GlobalScope.launch(Dispatchers.IO) {
            mSocket = Socket(server, port)
            mOut = DataOutputStream(mSocket!!.getOutputStream())
            mIn = BufferedReader(InputStreamReader(mSocket!!.getInputStream()))
        }
    }


    fun closeConnection() {
        if (mSocket != null) mSocket!!.close()
        mIsAuthorized = false
    }

    fun onAuthorize(action: MapAction<List<User>>) {
        GlobalScope.launch(Dispatchers.IO) {
            val users = authorize()
            withContext(Dispatchers.Main) {
                action.call(users)
            }
        }
    }

    fun onUpdate(action: MapAction<User>) {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val user = update()
                if (user != null) {
                    withContext(Dispatchers.Main) {
                        action.call(user)
                    }
                }
            }
        }
    }

    fun onError(action: MapAction<String>) {
        mOnError = action
    }

    private suspend fun authorize(): List<User> {
        var response = ""
        try {
            response = authorizeCommand()
        } catch (e: SocketException) {
            withContext(Dispatchers.Main) {
                if (mOnError != null) mOnError!!.call(e.message!!)
            }
        }

        if(response.startsWith(Command.USERLIST)) {
            val usersString = response.replace(Command.USERLIST, "").trim()
            try {
                mUsers.addAll(UsersParser().parse(usersString))
            } catch (e: MapException) {

            }
            mIsAuthorized = true
        }
        return mUsers;
    }

    private suspend fun authorizeCommand(): String {
        return withContext(Dispatchers.IO) {
            mOut!!.writeBytes("${Command.AUTHORIZE} ${Config.EMAIL}\n")
            mIn!!.readLine()
        }
    }

    @Nullable private fun update(): User? {
        if (!mIsAuthorized  || mSocket!!.isClosed) return null
        val response: String
        try {
            response = mIn!!.readLine()
        } catch (e: SocketException) {
            e.printStackTrace()
            return null
        }
        println(response)
        if(response.startsWith(Command.UPDATE)) {
            val userUpdatedString = response.replace(Command.UPDATE, "").trim()
            val u = userUpdatedString.split(",")
            if (u.size == 3) {
                val id = u.get(0).toInt()
                val lat = u.get(1).toDouble()
                val lon = u.get(2).toDouble()
                val user = getUser(id)
                if (user != null) {
                    user.lat = lat
                    user.lon = lon
                    return user
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
}
package com.example.map

import com.example.map.presenter.MapsPresenter
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun usersParserTest() {
        //val presenter = MapsPresenter("", 0)
        val presenter = MapsPresenter()
        val usersString = "11,Name1,http://someurl.jpg,56.9495677035,24.1064071655;12,Name2,http://someurl.jpg,56.9495677035,24.1064071655;"
        val users = presenter.parseUsers(usersString)
        assertEquals(users.size, 2)
        assertEquals(users[0].id, 11)
        assertEquals(users[0].username, "Name1")
        assertEquals(users[1].imageUrl, "http://someurl.jpg")
        assertEquals(users[0].lon, 56.9495677035f)
    }

 //   //TODO check parsing
 //   fun startServer() {
 //       thread {
 //           val server = ServerSocket(5051)
 //           server.run {
 //               GlobalScope.launch(Dispatchers.IO) {
 //                   val conSocket = server.accept();
 //                   //delay(timeMillis = 500)
 //                   val inFromClient = BufferedReader(InputStreamReader(conSocket.getInputStream()))
 //                   val outToClient = DataOutputStream(conSocket.getOutputStream())
 //                   val message: String = inFromClient.readLine()
 //                   if (message.contains(Command.AUTHORIZE)) {
 //
 //                   }
//
//
 //                   true
 //               }
 //           }
 //       }
 //   }
}

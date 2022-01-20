package com.example.grandtheftradio.network

import android.content.Context
import com.example.grandtheftradio.db.writeRadioDataDate
import com.example.grandtheftradio.entities.Radio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking

class FirebaseRemoteRadioDataSource : RemoteRadioDataSource {
    private val mDatabase = FirebaseDatabase.getInstance()

    @ExperimentalCoroutinesApi
    override suspend fun getRadios(context: Context)= callbackFlow<List<Radio>> {
        val databaseReference = mDatabase.getReference("stations")
        val gamesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Radio>()
                snapshot.children.forEach{
                    list.add((it.getValue(Radio::class.java) as Radio))
                }
                runBlocking {
                    writeRadioDataDate(context)
                }
                this@callbackFlow.trySendBlocking(list)
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.cancel("Error getting data", error.toException())
            }

        }
        databaseReference.addValueEventListener(gamesListener)

        awaitClose {
            databaseReference.removeEventListener(gamesListener)
        }
    }
}
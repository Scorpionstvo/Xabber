package com.xabber.presentation.application.fragments.message

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xabber.data.dto.MessageDto
import com.xabber.data.dto.MessageState
import com.xabber.data.dto.Sender
import java.util.*

class MessageViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<MessageDto>>()
    val messages: LiveData<List<MessageDto>> = _messages

    init {
        _messages.value = listOf(
                MessageDto(
                    "1",
                    "qwe1",
                    "qwe1",
                    Sender("first@sender.msg"),
                    UUID.randomUUID().toString(),
                    -900290400,
                    -777804960,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    MessageState.SENT,
                    null,
                    "Test 1: ihnofgwirhdskkkghoknvgioerhnR" +
                            "BEJIODBFJEOITBJE" +
                            "BT" +
                            "JKIOJTRIOPTFGBT",
                ),
                MessageDto(
                    "1",
                    "qwe1",
                    "qwe1",
                    Sender("second@sender.msg", "Second"),
                    UUID.randomUUID().toString(),
                    -900290400,
                    -777804960,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    MessageState.SENT,
                    null,
                    "Test 2: qkuyhscoiahfohflsjvonnvhdfg",
                ),
                MessageDto(
                    "1",
                    "qwe1",
                    "owner",
                    Sender("owner"),
                    UUID.randomUUID().toString(),
                    -900290400,
                    -777804960,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    MessageState.SENT,
                    null,
                    "Test 1: ihnofgwirhdskkkghoknvgioerhnR" +
                            "BEJIODBFJEOITBJE" +
                            "BT" +
                            "JKIOJTRIOPTFGBT",
                ),
                MessageDto(
                    "1",
                    "qwe1",
                    "owner",
                    Sender("owner"),
                    UUID.randomUUID().toString(),
                    -900290400,
                    -777804960,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    MessageState.SENT,
                    null,
                    "Test 2: qkuyhscoiahfohflsjvonnvhdfg",
                )
            )


        }
}
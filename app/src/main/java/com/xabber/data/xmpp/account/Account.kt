package com.xabber.data.xmpp.account

import android.os.Parcel
import android.os.Parcelable

class Account(
    val owner: String?,
    val name: String?,
    val jid: String?,
    val colorResId: Int,
    val avatar: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(owner)
        parcel.writeString(name)
        parcel.writeString(jid)
        parcel.writeInt(colorResId)
        parcel.writeInt(avatar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account {
            return Account(parcel)
        }

        override fun newArray(size: Int): Array<Account?> {
            return arrayOfNulls(size)
        }
    }
}
package com.xabber.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.osmdroid.util.GeoPoint

data class XabberAccountDto(
    @Expose
    @SerializedName("username")
    var username: String? = null,
    @Expose
    @SerializedName("registration_date")
    var registrationDate: String? = null,
    @Expose
    @SerializedName("domain")
    var domain: String? = null,
    @Expose
    @SerializedName("last_name")
    var lastName: String? = null,
    @Expose
    @SerializedName("full_id")
    var fullId: String? = null,
    @Expose
    @SerializedName("can_register_team")
    var canRegisterTeam: Boolean? = null,
    @Expose
    @SerializedName("has_debt")
    var hasDebt: Boolean? = null,
    @Expose
    @SerializedName("xmpp_users")
    var xmppUsers: ArrayList<XmppUsers> = arrayListOf(),
    @Expose
    @SerializedName("email_list")
    var emailList: ArrayList<String> = arrayListOf(),
    @Expose
    @SerializedName("has_password")
    var hasPassword: Boolean? = null,
    @Expose
    @SerializedName("account_status")
    var accountStatus: String? = null,
    @Expose
    @SerializedName("timezone")
    var timezone: String? = null,
    @Expose
    @SerializedName("preferences")
    var preferences: Preferences? = Preferences(),
    @Expose
    @SerializedName("social_bindings")
    var socialBindings: ArrayList<String> = arrayListOf(),
    @Expose
    @SerializedName("first_name")
    var firstName: String? = null,
    @Expose
    @SerializedName("language_verbose")
    var languageVerbose: String? = null,
    @Expose
    @SerializedName("language")
    var language: String? = null,
    @Expose
    @SerializedName("can_register_free_xmpp_account")
    var canRegisterFreeXmppAccount: Boolean? = null,
    @Expose
    @SerializedName("token")
    var token: String? = null,
    @Expose
    @SerializedName("my_teams")
    var myTeams: ArrayList<String> = arrayListOf(),
    @Expose
    @SerializedName("pk")
    var pk: Int? = null,
    @Expose
    @SerializedName("transferred_teams")
    var transferredTeams: ArrayList<String> = arrayListOf(),
    @Expose
    @SerializedName("type")
    var type: String? = null,
    @Expose
    @SerializedName("default_services")
    var defaultServices: DefaultServices? = DefaultServices(),
    @Expose
    @SerializedName("xmpp_binding")
    var xmppBinding: XmppBinding? = XmppBinding()
)

data class XmppUsers(
    @Expose
    @SerializedName("id") var id: Int? = null,
    @Expose
    @SerializedName("jid")
    var jid: String? = null,
    @Expose
    @SerializedName("type")
    var type: String? = null,
    @Expose
    @SerializedName("username")
    var username: String? = null,
    @Expose
    @SerializedName("host")
    var host: String? = null,
    @Expose
    @SerializedName("has_debt")
    var hasDebt: Boolean? = null
)

data class Preferences(
    @Expose
    @SerializedName("email_when_expires")
    var emailWhenExpires: Boolean? = null,
    @Expose
    @SerializedName("email_news") var emailNews: Boolean? = null
)

data class DefaultServices(
    @Expose
    @SerializedName("max_free_xmpp_users")
    var maxFreeXmppUsers: Int? = null
)

data class XmppBinding(
    @Expose
    @SerializedName("id")
    var id: Int? = null,
    @Expose
    @SerializedName("verified")
    var verified: Boolean? = null,
    @Expose
    @SerializedName("is_native")
    var isNative: Boolean? = null,
    @Expose
    @SerializedName("jid")
    var jid: String? = null
)


data class Place(
    @SerializedName("display_name") val displayName: String,
    val lon: Double,
    val lat: Double,
    val address: Address? = null,
)

fun Place.toGeoPoint() = GeoPoint(lat, lon)

val Place.prettyName: String
get() = address?.prettyAddress?.takeIf { it.isNotEmpty() } ?: displayName


data class Address(
    @SerializedName("house_number") val houseNumber: String? = null,
    val road: String? = null,
    val state: String? = null,
    val neighbourhood: String? = null,
    val allotments: String? = null,
    val village: String? = null,
    val city: String? = null,
    val country: String? = null,
)

private val Address.prettyAddress: String
    get() = listOfNotNull(
        road,
        houseNumber?.takeIf { !road.isNullOrEmpty() },
        neighbourhood,
        allotments,
        village,
        city,
        state,
        country?.takeIf { road.isNullOrEmpty() }
    ).joinToString(separator = ", ")
package com.xabber.presentation.application.fragments.chatlist

import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xabber.R
import com.xabber.data.dto.ChatListDto
import com.xabber.data.xmpp.messages.MessageSendingState
import com.xabber.data.xmpp.presences.ResourceStatus
import com.xabber.data.xmpp.presences.RosterItemEntity
import com.xabber.databinding.ItemChatBinding
import com.xabber.presentation.application.activity.UiChanger
import com.xabber.presentation.application.util.DateFormatter

class ChatViewHolder(
    private val binding: ItemChatBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(chatList: ChatListDto, listener: ChatListAdapter.ChatListener) {
        with(binding) {

            chatName.text = chatList.displayName
            if (chatList.pinnedDate > 0) {
                chatGround.setBackgroundColor(
                    itemView.resources.getColor(
                        R.color.grey_100,
                        itemView.context.theme
                    )
                )
            } else {
                chatGround.setBackgroundColor(
                    itemView.resources.getColor(
                        R.color.white,
                        itemView.context.theme
                    )
                )

            }

            chatMuted.isVisible = chatList.muteExpired > 0
            chatPinned.isVisible = chatList.pinnedDate > 0
            if (chatList.unreadString != null) {
                unreadMessagesWrapper.isVisible = chatList.unreadString.isNotEmpty()

                unreadMessagesCount.text = chatList.unreadString
            }


            var image: Int? = null
            var tint: Int? = null
            if (chatList.unreadString != null) chatStatusImage.isVisible =
                (chatList.unreadString.isEmpty())
            when (chatList.lastMessageState) {
                MessageSendingState.Sending -> {
                    tint = R.color.grey_500
                    image = R.drawable.ic_clock_outline
                }
                MessageSendingState.Sended -> {
                    tint = R.color.grey_500
                    image = R.drawable.ic_check_green
                }
                MessageSendingState.Deliver -> {
                    tint = R.color.green_500
                    image = R.drawable.ic_check_green
                }
                MessageSendingState.Read -> {
                    tint = R.color.green_500
                    image = R.drawable.ic_check_all_green
                }
                MessageSendingState.Error -> {
                    tint = R.color.red_500
                    image = R.drawable.ic_exclamation_mark_outline
                }
                MessageSendingState.NotSended -> {
                    tint = R.color.grey_500
                    image = R.drawable.ic_clock_outline
                }
                MessageSendingState.Uploading -> {
                    tint = R.color.blue_500
                    image = R.drawable.ic_clock_outline
                }
                MessageSendingState.None -> {
                    chatStatusImage.isVisible = false
                }
            }
            if (tint != null && image != null) {
                Glide.with(itemView)
                    .load(image)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .into(chatStatusImage)
                chatStatusImage.setColorFilter(
                    ContextCompat.getColor(itemView.context, tint),
                    PorterDuff.Mode.SRC_IN
                )
            }


            //     profileDivider.setBackgroundColor(
//                    itemView.resources.getColor(
//                        chatList.colorId,
//                        itemView.context.theme
//                    )
//                )
//
//                chatImage.setBackgroundColor(
//
//                    itemView.resources.getColor(
//                        chatList.colorId,
//                        itemView.context.theme
//                    )
//                )

            Glide.with(itemView)
                .load(R.drawable.ic_avatar_placeholder)
                .centerCrop()
                .skipMemoryCache(true)
                .into(chatImage)


            chatTimestamp.text = DateFormatter.dateFormat(chatList.lastMessageDate.toString())

            chatMessage.text = chatList.lastMessageBody

            chatSyncImage.isVisible = chatList.isSynced
            //     chatMessage.text = if (chatList.) "?????????????????????? 229??86 KiB" else chatList.lastMessage


            if (chatList.entity == RosterItemEntity.Contact) {
                val icon = when (chatList.status) {
                    ResourceStatus.Offline -> R.drawable.status_online
                    ResourceStatus.Away -> R.drawable.status_away
                    ResourceStatus.Online -> R.drawable.status_online
                    ResourceStatus.Xa -> R.drawable.ic_status_xa
                    ResourceStatus.Dnd -> R.drawable.status_dnd
                    ResourceStatus.Chat -> R.drawable.status_chat

                }

                chatStatus16.isVisible = false
                chatStatus14.isVisible = true
                chatStatus14.setImageResource(icon)
            } else {
                val icon =
                    when (chatList.entity) {
                        RosterItemEntity.Server -> {
                            when (chatList.status) {
                                ResourceStatus.Offline -> R.drawable.status_server_unavailable
                                else -> R.drawable.status_server_online
                            }
                        }
                        RosterItemEntity.Bot -> {
                            when (chatList.status) {
                                ResourceStatus.Offline -> R.drawable.status_bot_unavailable
                                ResourceStatus.Away -> R.drawable.status_bot_away
                                ResourceStatus.Online -> R.drawable.status_bot_online
                                ResourceStatus.Xa -> R.drawable.status_bot_xa
                                ResourceStatus.Dnd -> R.drawable.status_bot_dnd
                                ResourceStatus.Chat -> R.drawable.status_bot_chat

                            }
                        }
                        RosterItemEntity.IncognitoChat -> {
                            when (chatList.status) {
                                ResourceStatus.Offline -> R.drawable.status_incognito_group_unavailable
                                ResourceStatus.Away -> R.drawable.status_incognito_group_away
                                ResourceStatus.Online -> R.drawable.status_incognito_group_online
                                ResourceStatus.Xa -> R.drawable.status_incognito_group_xa
                                ResourceStatus.Dnd -> R.drawable.status_incognito_group_dnd
                                ResourceStatus.Chat -> R.drawable.status_incognito_group_chat

                            }
                        }


                        RosterItemEntity.Groupchat -> {
                            when (chatList.status) {
                                ResourceStatus.Offline -> R.drawable.status_public_group_unavailable
                                ResourceStatus.Away -> R.drawable.status_public_group_away
                                ResourceStatus.Online -> R.drawable.status_public_group_online
                                ResourceStatus.Xa -> R.drawable.status_public_group_xa
                                ResourceStatus.Dnd -> R.drawable.status_public_group_dnd
                                ResourceStatus.Chat -> R.drawable.status_public_group_chat
                            }
                        }

                        RosterItemEntity.PrivateChat -> {
                            when (chatList.status) {
                                ResourceStatus.Offline -> R.drawable.status_private_chat_unavailable
                                ResourceStatus.Away -> R.drawable.status_private_chat_away
                                ResourceStatus.Online -> R.drawable.status_private_chat_online
                                ResourceStatus.Xa -> R.drawable.status_private_chat_xa
                                ResourceStatus.Dnd -> R.drawable.status_private_chat_dnd
                                ResourceStatus.Chat -> R.drawable.status_private_chat
                            }
                        }

                        else -> {
                            0
                        }


                    }
                chatStatus16.isVisible = true
                chatStatus14.isVisible = false
                chatStatus16.setImageResource(icon)

            }


            //   RosterItemEntity.PRIVATE_CHAT ->


            //     val chatStatusContainer = chatStatusContainer12
            //   if (chat.entity in listOf(CONTACT, ISSUE))
            //        chatStatusContainer12
            //    else
            //       chatStatusContainer16

            //        chatStatusContainer.isVisible = true
            //    chatStatusContainer.setCardBackgroundColor(
            //        itemView.resources.getColor(
            //            chat.getStatusColor(),
            //             itemView.context.theme
            //         )
            //        )


            //      if (chat.userNickname != null) {
            //          val spannable = SpannableString("${chat.userNickname}\n${chat.message}")
            //          spannable.setSpan(
            //              ForegroundColorSpan(
            //                  itemView.resources.getColor(
            ////                      R.color.grey_900,
            //                     itemView.context.theme
            // //                   )
            //               ),
            //                0,
            //               chat.userNickname.length,
            //             Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            //          )
            //           chatMessage.text = spannable
            //       } else
            //    chatMessage.text = chat.message


            if (chatList.DraftMessage != null) {
                val spannable = SpannableString("Drafted: ${chatList.DraftMessage}")
                spannable.setSpan(
                    ForegroundColorSpan(
                        itemView.resources.getColor(
                            R.color.red_500,
                            itemView.context.theme
                        )
                    ),
                    0,
                    8,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                chatMessage.text = spannable
            }

            chatSyncImage.isVisible = chatList.isSynced
            if (chatList.isSystemMessage)
                chatMessage.setTypeface(null, Typeface.ITALIC)

            itemView.setOnClickListener {
                listener.onClickItem(chatList.displayName)

            }

            chatImage.setBackgroundResource(UiChanger.getMask().size56)
            chatImage.setOnClickListener {
                listener.onClickAvatar(chatList.displayName)
            }

            itemView.setOnLongClickListener {
                val popup = PopupMenu(itemView.context, itemView, Gravity.RIGHT)
                if (chatList.pinnedDate.equals(0)) popup.inflate(R.menu.context_menu_chat)
                else popup.inflate(R.menu.context_menu_chat2)

                popup.setOnMenuItemClickListener {

                    when (it.itemId) {
                        R.id.unpin -> listener.unPinChat(chatList.id)
                        R.id.to_pin -> {
                            listener.pinChat(chatList.id)
                        }
                        R.id.turn_of_notifications -> {
                            listener.turnOfNotifications(chatList.id)
                        }
                        R.id.customise_notifications -> {
                            listener.openSpecialNotificationsFragment()
                        }
                        R.id.delete_chat -> {
                            listener.deleteChat(chatList.id)
                        }

                    }
                    true
                }
                popup.show()
                true
            }


            fun onSwipeChatItem() {
//listener.swipeItem(chat.id)

            }
        }
    }
}
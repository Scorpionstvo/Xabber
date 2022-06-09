package com.xabber.presentation.application.fragments.message

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.CustomPopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import com.xabber.R
import com.xabber.data.dto.MessageDto
import com.xabber.data.util.dp
import com.xabber.databinding.ItemMessageIncomingBinding
import com.xabber.presentation.application.util.StringUtils
import java.util.*

class IncomingMessageVH(
    private val binding: ItemMessageIncomingBinding,
    private val listener: MessageAdapter.Listener,
) : BasicViewHolder(binding.root, listener) {
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi", "ResourceAsColor")
    override fun bind(
        messageDto: MessageDto,
        isNeedTail: Boolean,
        needDay: Boolean,
        showCheckbox: Boolean
    ) {
        Log.d("show", "$showCheckbox")
        // text & appearance
        binding.tvContent.isVisible = messageDto.messageBody != null
        binding.tvContent.text = messageDto.messageBody


        //  tvContent.setTextAppearance(SettingsManager.chatsAppearanceStyle()) - берем из класса настроек

        // date
        binding.messageDate.tvDate.isVisible = needDay
        if (binding.messageDate.tvDate.isVisible) {
            binding.messageDate.tvDate.text =
                StringUtils.getDateStringForMessage(messageDto.sentTimestamp)
        }

        // time
        val date = Date(messageDto.sentTimestamp)
        val time = StringUtils.getTimeText(binding.tvSendingTime.context, date)
        binding.tvSendingTime.text = time

        // for group chat
        binding.tvContactName.isVisible = messageDto.isGroup && isNeedTail
        if (binding.tvContactName.isVisible) binding.tvContactName.text = messageDto.owner
        binding.avatarContact.isVisible = messageDto.isGroup && isNeedTail

        // checkbox
        binding.checkboxIncoming.isVisible = showCheckbox

        // margins and paddings
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val marginRight =
            if (messageDto.isGroup && !showCheckbox) 46.dp else if (messageDto.isGroup && showCheckbox) 12.dp else 40.dp
        if (!messageDto.isGroup || binding.avatarContact.isVisible) {
            params.setMargins(
                if (isNeedTail) 2 else 24,
                0,
                marginRight,
                0
            )
        } else if (messageDto.isGroup && !binding.avatarContact.isVisible) {
            params.setMargins(
                58.dp,
                0,
                marginRight,
                0
            )
        }

        binding.balloon.layoutParams = params
        binding.balloon.setPadding(if (isNeedTail) 54 else 26, 26, 26, 26)


        // background
        val balloonDrawable = ResourcesCompat.getDrawable(
            itemView.resources,
            if (isNeedTail)
                R.drawable.msg_in
            else
                R.drawable.msg,
            itemView.context.theme
        )!!
        balloonDrawable.setColorFilter(
            itemView.resources.getColor(
                R.color.blue_100,
                itemView.context.theme
            ), PorterDuff.Mode.MULTIPLY
        )
        binding.balloon.background = balloonDrawable

        // shadow
        val shadowDrawable = ResourcesCompat.getDrawable(
            itemView.resources,
            if (isNeedTail)
                R.drawable.msg_in_shadow
            else
                R.drawable.msg_shadow,
            itemView.context.theme
        )
        shadowDrawable?.setColorFilter(
            itemView.resources.getColor(
                R.color.black,
                itemView.context.theme
            ), PorterDuff.Mode.MULTIPLY
        )


        //   val popupMenu = createPopupMenu(messageDto, binding.root)

        binding.root.setOnClickListener {
            if (showCheckbox) {
                binding.frameLayoutBlackout.setBackgroundResource(R.color.selected)
                binding.tvContent.setTextIsSelectable(showCheckbox)
            } else {
                //  binding.checkboxIncoming.isChecked = !binding.checkboxIncoming.isChecked
            }

        }


        binding.root.setOnLongClickListener {

            if (!showCheckbox) {

                listener.onLongClick(messageDto.primary)
            } else {
                binding.checkboxIncoming.isChecked = !binding.checkboxIncoming.isChecked
                binding.frameLayoutBlackout.setBackgroundResource(R.color.selected)
                binding.tvContent.setTextIsSelectable(showCheckbox)
            }
            true
        }
    }

    @SuppressLint("RestrictedApi")
    fun createPopupMenu(messageDto: MessageDto, view: View): CustomPopupMenu {
        val popup = CustomPopupMenu(view.context, view, Gravity.CENTER)
        if (messageDto.isOutgoing) popup.inflate(R.menu.context_menu_message_outgoing)
        else popup.inflate(R.menu.context_menu_message_incoming)

        val menuHelper = MenuPopupHelper(view.context, popup.menu as MenuBuilder, binding.root)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.copy -> {
                    val text = binding.tvContent.text.toString()
                    listener.copyText(text)
                }
                R.id.forward -> {
                    listener.forwardMessage(messageDto)
                }
                R.id.reply -> {
                    listener.replyMessage(messageDto)
                }
                R.id.delete_message -> {
                    listener.deleteMessage(messageDto)
                }
            }
            true
        }
        return popup
    }
}




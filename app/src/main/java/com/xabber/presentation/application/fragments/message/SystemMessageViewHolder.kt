package com.xabber.presentation.application.fragments.message

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.xabber.data.dto.MessageDto
import com.xabber.databinding.ItemMessageSystemBinding
import com.xabber.presentation.application.util.StringUtils

class SystemMessageViewHolder(
    private val binding: ItemMessageSystemBinding
) : BasicViewHolder(
    binding.root
) {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun bind(message: MessageDto, isNeedTail: Boolean, needDay: Boolean) {
        super.bind(message, isNeedTail, needDay)
        binding.messageText.isVisible = message.messageBody != null
        if (message.messageBody != null) binding.messageText.text = message.messageBody
        binding.messageDate.tvDate.isVisible = needDay
        binding.messageDate.tvDate.text = StringUtils.getDateStringForMessage(message.sentTimestamp)
    }
}

package com.xabber.presentation.application.fragments.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.xabber.data.dto.MessageDto
import com.xabber.databinding.ItemMessageSystemBinding

class SystemMessageMessageVH(
    private val binding: ItemMessageSystemBinding
) : BasicMessageVH(
    binding.root,
) {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun bind(
        messageDto: MessageDto,
        isNeedTail: Boolean,
        needDay: Boolean,
        showCheckbox: Boolean,
        isNeedTitle: Boolean
    ) {
        super.bind(messageDto, isNeedTail, needDay, showCheckbox, isNeedTitle)
        binding.messageText.isVisible = messageDto.messageBody != null
        if (messageDto.messageBody != null) binding.messageText.text = messageDto.messageBody

    }
}

package com.xabber.presentation.application.fragments.message

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xabber.R
import com.xabber.data.dto.FileDto
import com.xabber.data.dto.MessageDto
import com.xabber.data.dto.MessageKind
import com.xabber.data.xmpp.messages.MessageDisplayType
import com.xabber.data.xmpp.messages.MessageSendingState
import com.xabber.databinding.FragmentMessageBinding
import com.xabber.presentation.application.contract.navigator
import com.xabber.presentation.application.fragments.DetailBaseFragment

class MessageFragment : DetailBaseFragment(R.layout.fragment_message), MessageAdapter.Listener,
    AttachDialog.Listener, ReplySwipeCallback.SwipeAction, FileAdapter.Listener {
    private val binding by viewBinding(FragmentMessageBinding::bind)
    private var messageAdapter: MessageAdapter? = null
    private var fileAdapter: FileAdapter? = null
    private val viewModel = MessageViewModel()
    var name: String = ""

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), ::onGotCameraPermissionResult
    )
  private fun onGotCameraPermissionResult(granted: Boolean) {
        if (granted) {
            takePhoto()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
               // askUserForOpeningAppSettings()
            } else {
                Toast.makeText(context, R.string.permission_denied_toast, Toast.LENGTH_SHORT).show()
            }
        }
    }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
        val   bitmap = data?.extras?.get("data") as Bitmap
   val files = ArrayList<FileDto>()
            files.add(0, FileDto(1, "", bitmap))

            binding.frameLayoutAttachedFiles.isVisible = true
            fileAdapter?.submitList(files)
            binding.buttonSendMessage.isVisible = files.size > 0
       //     Log.d("files", "${files.size}, ${binding.attachedFiles.adapter}")
        //    fileAdapter?.updateAdapter(files)

        }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, 1)
        } catch(e: ActivityNotFoundException) {}
    }
    companion object {
        fun newInstance(_name: String) = MessageFragment().apply {
            arguments = Bundle().apply {
                putString("_name", _name)
                name = _name
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name", "")
        }
        binding.messageUserName.text = name
        initToolbarActions()
        initRecyclerView()
        subscribeViewModelData()
        initInputLayoutActions()
        fileAdapter = FileAdapter(this)

        binding.attachedFiles.adapter = fileAdapter
        binding.attachedFiles.layoutManager =
           LinearLayoutManager(context)
    }

    private fun initToolbarActions() {
        Log.d("sliding", "fragmentToolbar ${navigator().slidingPaneLayoutIsOpen()}")
        if (navigator().slidingPaneLayoutIsOpen()) binding.messageIconBack.setImageDrawable(
            ContextCompat.getDrawable(binding.root.context, R.drawable.ic_material_close_24)
        )
        else binding.messageIconBack.setImageDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.ic_arrow_left
            )
        )
        binding.messageIconBack.setOnClickListener {
            navigator().closeDetail()
        }
        Log.d("sliding", "fragmentToolbar 2 ${navigator().slidingPaneLayoutIsOpen()}")
    }

    private fun initRecyclerView() {
        messageAdapter = MessageAdapter(this)
        binding.messageList.adapter = messageAdapter
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        binding.messageList.layoutManager = linearLayoutManager
        addSwipeCallback()
    }

    private fun addSwipeCallback() {
        val replySwipeCallback = ReplySwipeCallback(binding.messageList.context)
        replySwipeCallback.setSwipeEnabled(true)
        replySwipeCallback.replySwipeCallback()
        ItemTouchHelper(replySwipeCallback).attachToRecyclerView(binding.messageList)

        binding.messageList.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    replySwipeCallback.onDraw(c)
                }
            })
    }

    private fun subscribeViewModelData() {
        viewModel.initList()
        viewModel.messages.observe(viewLifecycleOwner) {
            it.sort()
            messageAdapter?.submitList(it)
        }
        viewModel.files.observe(viewLifecycleOwner) {
         //   fileAdapter?.updateAdapter(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initInputLayoutActions() {
        chatInputAddListener()
        binding.buttonEmoticon.setOnClickListener { }

        binding.buttonAttach.setOnClickListener {
            val dialog = AttachDialog(this)
            navigator().showBottomSheetDialog(dialog)
        }

        binding.buttonSendMessage.setOnClickListener {
            var messageKindDto: MessageKind? = null
            if (binding.answer.isVisible) {
                messageKindDto = MessageKind(
                    "id",
                    binding.replyMessageTitle.text.toString(),
                    binding.replyMessageContent.text.toString()
                )
            }
            val text = binding.chatInput.text.toString().trim()
            binding.chatInput.text?.clear()
            val timeStamp = System.currentTimeMillis()
            viewModel.insertMessage(
                MessageDto(
                    "151515",
                    true,
                    "Алексей Иванов",
                    "Геннадий Белов",
                    text,
                    MessageSendingState.Deliver,
                    timeStamp,
                    null,
                    MessageDisplayType.Text,
                    false,
                    false,
                    null,
                    false, messageKindDto
                )
            )
            binding.answer.isVisible = false
            messageAdapter?.notifyDataSetChanged()
            scrollDown()
        }
        binding.buttonRecord.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                binding.groupRecord.isVisible = true
                binding.buttonEmoticon.isVisible = false
                binding.buttonAttach.isVisible = false
                binding.chatInput.isVisible = false
                binding.recordChronometer.base = SystemClock.elapsedRealtime()
                binding.recordChronometer.start()
                AudioRecorder.startRecord()
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                binding.groupRecord.isVisible = false
                binding.buttonEmoticon.isVisible = true
                binding.buttonAttach.isVisible = true
                binding.chatInput.isVisible = true

                binding.recordChronometer.stop()
                binding.recordChronometer.base = SystemClock.elapsedRealtime()
                AudioRecorder.stopRecord { file ->
                    Toast.makeText(context, "${file == null}", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        binding.messageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) binding.btnDownward.animate()
                    .translationY(binding.btnDownward.height + binding.btnDownward.marginBottom.toFloat())
                else if (dy > 0) binding.btnDownward.animate()
                    .translationY(0f)
            }
        })
        binding.btnDownward.setOnClickListener {
            scrollDown()
        }

    }

    private fun scrollDown() {
        binding.messageList.scrollToPosition(0)
    }

    private fun updateTopDateIfNeed() {
        val layoutManager = binding.messageList.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
// val message : MessageDto = messageAdapter!!.getItem(position)
// if (message != null)
// binding.tvTopDate.setText(StringUtils.getDateStringForMessage(message.t)
    }


    private fun chatInputAddListener() {
        with(binding) {
            chatInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().trim().isNotEmpty()) {
                        buttonRecord.isVisible = false
                        buttonAttach.isVisible = false
                        buttonSendMessage.isVisible = true
                    } else {
                        buttonRecord.isVisible = true
                        buttonAttach.isVisible = true
                        buttonSendMessage.isVisible = false
                    }
                }
            })
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        messageAdapter = null
        fileAdapter = null
        AudioRecorder.releaseRecorder()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("name", name)
    }

    override fun copyText(text: String) {
        val clipBoard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("", text)
        clipBoard.setPrimaryClip(clipData)
    }

    override fun forwardMessage(messageDto: MessageDto) {
        //    navigator().showChatFragment()
    }

    override fun replyMessage(messageDto: MessageDto) {
        binding.answer.isVisible = true
        binding.replyMessageTitle.text = messageDto.owner
        binding.replyMessageContent.text = messageDto.messageBody
        binding.close.setOnClickListener {
            binding.replyMessageTitle.text = ""
            binding.replyMessageContent.text = ""
            binding.answer.isVisible = false
        }
    }

    override fun editMessage(primary: String) {
// binding.chatInput.text = primary
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onLongClick(primary: String) {
       messageAdapter?.showCheckbox(true)
        messageAdapter?.notifyDataSetChanged()
    }

    override fun deleteMessage(messageDto: MessageDto) {
        viewModel.deleteMessage(messageDto)
        messageAdapter?.notifyDataSetChanged()
    }

    override fun onRecentPhotosSend(paths: HashSet<String>?) {

    }

    override fun onGalleryClick() {

    }

    override fun onFilesClick() {

    }

    override fun onCameraClick() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onLocationClick() {

    }

    override fun onFullSwipe(position: Int) {

        replyMessage(
            MessageDto(
                "22222",
                true,
                "Ann",
                "Геннадий Белов",
                "Алексей присоединился к чату",
                MessageSendingState.Read,
                1654234345585,
                null,
                MessageDisplayType.System,
                false,
                false,
                null, false
            )
        )
    }

    override fun deleteFile() {

    }

//    override fun deleteFile() {
//
//    }

}

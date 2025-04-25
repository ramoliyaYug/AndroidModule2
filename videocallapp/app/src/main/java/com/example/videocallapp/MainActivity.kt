package com.example.videocallapp

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import java.util.Collections

class MainActivity : AppCompatActivity() {
    lateinit var currentusernametextview : TextView
    lateinit var targetusernameinput : EditText
    lateinit var voicecallbtn : ZegoSendCallInvitationButton
    lateinit var videoCallbtn : ZegoSendCallInvitationButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currentusernametextview = findViewById(R.id.current_username_textview)
        targetusernameinput = findViewById(R.id.taget_username_input)
        voicecallbtn = findViewById(R.id.voice_call_btn)
        videoCallbtn = findViewById(R.id.video_call_btn)
        val username = intent.getStringExtra("username")
        currentusernametextview.text = "Hello "+ username
        targetusernameinput.addTextChangedListener{
            val targetusername = targetusernameinput.text.toString()
            setupvoicecall(targetusername)
            setupvideocall(targetusername)
        }
    }
    fun setupvoicecall(username : String){
        voicecallbtn.setIsVideoCall(false)
        voicecallbtn.resourceID = "zego_uikit_call"
        voicecallbtn.setInvitees(Collections.singletonList(ZegoUIKitUser(username,username)))
    }
    fun setupvideocall(username: String){
        videoCallbtn.setIsVideoCall(true)
        videoCallbtn.resourceID = "zego_uikit_call"
        videoCallbtn.setInvitees(Collections.singletonList(ZegoUIKitUser(username,username)))
    }
}
package com.kelsos.mbrc.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.VolumeModifyUseCase
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder
import com.kelsos.mbrc.preferences.SettingsManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class RemoteBroadcastReceiver : BroadcastReceiver(), KoinComponent {

  private val settingsManager: SettingsManager by inject()
  private val volumeModifyUseCase: VolumeModifyUseCase by inject()
  private val userActionUseCase: UserActionUseCase by inject()

  /**
   * Initialized and installs the IntentFilter listening for the SONG_CHANGED
   * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
   * Android operating system.
   */
  fun filter(): IntentFilter {
    return IntentFilter().apply {
      addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
      addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
      addAction(RemoteViewIntentBuilder.PLAY_PRESSED)
      addAction(RemoteViewIntentBuilder.NEXT_PRESSED)
      addAction(RemoteViewIntentBuilder.CLOSE_PRESSED)
      addAction(RemoteViewIntentBuilder.PREVIOUS_PRESSED)
      addAction(RemoteViewIntentBuilder.CANCELLED_NOTIFICATION)
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    when {
      TelephonyManager.ACTION_PHONE_STATE_CHANGED == intent.action -> onPhoneStateChange(intent)
      WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action -> onWifiChange(intent)
      RemoteViewIntentBuilder.PLAY_PRESSED == intent.action -> postAction(
        UserAction(
          Protocol.PlayerPlayPause,
          true
        )
      )
      RemoteViewIntentBuilder.NEXT_PRESSED == intent.action -> postAction(
        UserAction(
          Protocol.PlayerNext,
          true
        )
      )
      RemoteViewIntentBuilder.CLOSE_PRESSED == intent.action -> {
        // bus.post(SessionNotificationManager.CancelNotificationEvent())
      }
      RemoteViewIntentBuilder.PREVIOUS_PRESSED == intent.action -> postAction(
        UserAction(
          Protocol.PlayerPrevious,
          true
        )
      )
      RemoteViewIntentBuilder.CANCELLED_NOTIFICATION == intent.action -> context.stopService(
        Intent(
          context,
          RemoteService::class.java
        )
      )
    }
  }

  private fun onWifiChange(intent: Intent) {
    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
    if (networkInfo.state == NetworkInfo.State.CONNECTED) {
      // bus.post(ChangeConnectionStateEvent(START))
    }
  }

  private fun onPhoneStateChange(intent: Intent) {
    val bundle = intent.extras ?: return
    val state = bundle.getString(TelephonyManager.EXTRA_STATE)
    if (TelephonyManager.EXTRA_STATE_RINGING.equals(state!!, ignoreCase = true)) {

      when (settingsManager.getCallAction()) {
        SettingsManager.PAUSE -> postAction(UserAction(Protocol.PlayerPause, true))
        SettingsManager.STOP -> postAction(UserAction(Protocol.PlayerStop, true))
        SettingsManager.NONE -> Unit
        SettingsManager.REDUCE -> volumeModifyUseCase.reduceVolume()
        else -> Unit
      }
    }
  }

  private fun postAction(data: UserAction) {
    userActionUseCase.perform(data)
  }
}
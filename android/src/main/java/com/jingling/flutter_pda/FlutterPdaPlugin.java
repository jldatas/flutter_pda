package com.jingling.flutter_pda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterPdaPlugin */
public class FlutterPdaPlugin implements MethodCallHandler, StreamHandler, FlutterPlugin {

  private Context applicationContext;
  private BroadcastReceiver chargingStateChangeReceiver;
  private MethodChannel methodChannel;
  private EventChannel eventChannel;
  private String scanAction = "com.jingling.flutter_pda";// 定义广播名

  Intent _intent = new Intent("com.android.scanner.service_settings");

  /**
   * Plugin registration.
   */
  @SuppressWarnings("deprecation")
  public static void registerWith(io.flutter.plugin.common.PluginRegistry.Registrar registrar) {
    final FlutterPdaPlugin instance = new FlutterPdaPlugin();
    instance.onAttachedToEngine(registrar.context(), registrar.messenger());
  }

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.applicationContext = applicationContext;
    methodChannel = new MethodChannel(messenger, "flutter_pda/method");
    eventChannel = new EventChannel(messenger, "flutter_pda/event");
    eventChannel.setStreamHandler(this);
    methodChannel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
    applicationContext = null;
    methodChannel.setMethodCallHandler(null);
    methodChannel = null;
    eventChannel.setStreamHandler(null);
    eventChannel = null;

  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      case "getSoundPlay":
        result.success( getSoundPlay());
        break;
      case "setSoundPlay":
        Boolean isSound = call.argument("isSound");
        setSoundPlay(isSound);
        break;
      case "getVibrate":
        System.out.print(getVibrate());
        result.success(getVibrate());
        break;
      case "setVibrate":
        Boolean isVibrate = call.argument("isVibrate");
        setVibrate(isVibrate);
        break;
      case "getSendMode":
        result.success(getSendMode());
        break;
      case "setSendMode":
        String sendMode = call.argument("sendMode");
        setSendMode(sendMode);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  @Override
  public void onListen(Object arguments, EventSink events) {
    chargingStateChangeReceiver = createChargingStateChangeReceiver(events);
    applicationContext.registerReceiver(
            chargingStateChangeReceiver, new IntentFilter(scanAction));
    initBoardCast();
  }

  @Override
  public void onCancel(Object arguments) {
    applicationContext.unregisterReceiver(chargingStateChangeReceiver);
    chargingStateChangeReceiver = null;
  }

  private BroadcastReceiver createChargingStateChangeReceiver(final EventSink events) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(scanAction)) {
          String code = intent.getStringExtra("scannerdata");
          events.success(code);
        }
      }
    };
  }
  
  private void initBoardCast() {
    //修改广播名称：修改扫描工具广播名，接收广播时也是这个广播名
    _intent.putExtra("action_barcode_broadcast", scanAction);
    //条码发送方式：广播；其他设置看文档
    _intent.putExtra("barcode_send_mode","BROADCAST");
    //键值，一般不改
    _intent.putExtra("key_barcode_broadcast","scannerdata");
    //声音
    _intent.putExtra("sound_play",true);
    //震动
    _intent.putExtra("viberate",true);
    //其他参数设置参照：Android扫描服务设置.doc
    applicationContext.sendBroadcast(_intent);
  }

  // 获取声音状态
  private Boolean getSoundPlay() {
    return _intent.getBooleanExtra("sound_play", true);
  }

  // 设置声音状态
  private void setSoundPlay(boolean enabled) {
    _intent.putExtra("sound_play",enabled);
    applicationContext.sendBroadcast(_intent);
  }

  // 获取振动状态
  private Boolean getVibrate() {
    return _intent.getBooleanExtra("viberate", true);
  }

  // 设置振动状态
  private void setVibrate(boolean enabled) {
    _intent.putExtra("viberate", enabled);
    applicationContext.sendBroadcast(_intent);
  }

  // 获取条码发送方式
  private String getSendMode() {
    return _intent.getStringExtra("barcode_send_mode");
  }

  // 设置条码发送方式
  private void setSendMode(String sendMode) {
    _intent.putExtra("barcode_send_mode", sendMode);
    applicationContext.sendBroadcast(_intent);
  }

  // TODO: 初始化或设置默认参数
  //修改扫描工具参数：广播名、条码发送方式、声音、震动等
  private void setBoardCastSetting(MethodCall call, Result result) {
    String actionName = call.argument("actionName");
    String sendMode = call.argument("sendMode");
    String keyValue = call.argument("keyValue");
    String isSound = call.argument("isSound");
    String isVibrate = call.argument("isVibrate");

    Intent intent = new Intent("com.android.scanner.service_settings");
    //修改广播名称：修改扫描工具广播名，接收广播时也是这个广播名
    intent.putExtra("action_barcode_broadcast", actionName);
    //条码发送方式：广播；其他设置看文档
    intent.putExtra("barcode_send_mode", sendMode);
    //键值，一般不改
    intent.putExtra("key_barcode_broadcast", keyValue);
    //声音
    intent.putExtra("sound_play", isSound);
    //震动
    intent.putExtra("viberate", isVibrate);
    //其他参数设置参照：Android扫描服务设置.doc
    applicationContext.sendBroadcast(intent);
  }
}
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_pda/flutter_pda.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  FlutterPda _flutterPda = FlutterPda();

  String _code = 'code';
  bool _isSound;
  bool _isVibrate;
  String _sendMode;

  @override
  void initState() {
    super.initState();
    _flutterPda.onPdaStateChanged.listen((code) {
      print(code);
      setState(() {
        _code = code;
      });
    });
    _initFlutterPda();
  }

  Future _initFlutterPda() async {
    bool isSound = await _flutterPda.isSoundPlay;
    bool isVibrate = await _flutterPda.isVibrate;
    String sendMode = await _flutterPda.sendMode;
    setState(() {
      _isSound = isSound;
      _isVibrate = isVibrate;
      _sendMode = sendMode;
    });
  }

  Future _onSoundPlay(isSound) async {
    setState(() {
      _isSound = isSound;
    });
    await _flutterPda.setSoundPlay(isSound);
  }

  Future _onVibrate(isVibrate) async {
    setState(() {
      _isVibrate = isVibrate;
    });
    await _flutterPda.setVibrate(isVibrate);
  }

  Future _onSendMode(sendMode) async {
    setState(() {
      _sendMode = sendMode;
    });
    await _flutterPda.setSendMode(sendMode);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Center(
        child: Column(children: [
          Row(children: [
            Text('扫描声音'),
            Switch(value: _isSound, onChanged: _onSoundPlay)
          ],),
          Row(children: [
            Text('振动'),
            Switch(value: _isVibrate, onChanged: _onVibrate)
          ],),
          Row(children: [
            Text('条码发送方式'),
          ],),
          Column(children: [
            Row(children: [
              Radio(value: "FOCUS", groupValue: _sendMode, onChanged: _onSendMode),
              Text('焦点录入'),
            ],),
            Row(children: [
              Radio(value: "BROADCAST", groupValue: _sendMode, onChanged: _onSendMode),
              Text('广播'),
            ],),
            Row(children: [
              Radio(value: "EMUKEY", groupValue: _sendMode, onChanged: _onSendMode),
              Text('模拟按键'),
            ],),
            Row(children: [
              Radio(value: "CLIPBOARD", groupValue: _sendMode, onChanged: _onSendMode),
              Text('剪切板'),
            ],),
          ],),
          Padding(padding: EdgeInsets.only(top: 0), child: Text(_code),),
        ],),
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
  }
}

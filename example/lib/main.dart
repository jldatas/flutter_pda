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
  StreamSubscription _flutterPdaStateSubscription;

  String _code = 'code';

  @override
  void initState() {
    super.initState();
    // 监听PDA
    _flutterPdaStateSubscription = _flutterPda.onPdaStateChanged.listen((code) {
      setState(() {
        _code = code;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Center(
        child: Column(children: [
          Padding(padding: EdgeInsets.only(top: 20), child: Text(_code),),
        ],),
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
    // 取消PDA
    if(_flutterPdaStateSubscription != null) {
      _flutterPdaStateSubscription.cancel();
    }
  }
}

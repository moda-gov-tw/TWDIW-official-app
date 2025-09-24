import 'package:flutter/services.dart';

class HelloWorld {
  static const platform = MethodChannel('hello_world_channel');
  static const BasicMessageChannel<String> channel =
      BasicMessageChannel('hello_world_channel', StringCodec());

  static void setupMessageHandler() {
    channel.setMessageHandler((message) async {
      print('Received message from Android: $message');
      return 'Hello from Flutter!';
    });
  }

  static Future<void> sendMessage(String message) async {
    final String response = await channel.send(message) as String;
    print('Response from Android: $response');
  }

  Future<String> getMessage() async {
    final String message = await platform.invokeMethod('getHelloWorldMessage');
    return message;
  }
}

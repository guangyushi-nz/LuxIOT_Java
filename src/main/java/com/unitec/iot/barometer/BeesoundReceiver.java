package com.unitec.iot.barometer;

import org.eclipse.paho.client.mqttv3.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class BeesoundReceiver {

    public static void main(String[] args) {
        String brokerUrl = "ssl://25ab2416fcd34f328cb70fb7ff3f5294.s1.eu.hivemq.cloud:8883";
        String username = "beesound";
        String password = "beesound";
        String topic = "greenhouse/bee";

        try {
            // Create an MQTT client instance
            MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId());

            // Set up MQTT connect options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            // Set up MQTT message callback
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Append the received chunk to the output file
                    System.out.println("Received chunk: " + message.getPayload().length + " bytes");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used in this example
                }
            });

            // Connect to the MQTT broker
            client.connect(options);

            // Subscribe to the topic
            client.subscribe(topic);

            // Wait for messages
            System.out.println("Waiting for audio file chunks...");

            // Keep the receiver running
            while (true) {
                Thread.sleep(1000);
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

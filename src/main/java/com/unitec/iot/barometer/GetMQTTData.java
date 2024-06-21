package com.unitec.iot.barometer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetMQTTData {

    private static JFrame frame;
    private static DynamicGraphicsJsonExample panel;

    public static void main(String[] args) throws MqttException {
        String broker = "tcp://mqtt-dashboard.com:1883";
        String topic = "topic_lenovo";
        String clientid = "clientId-ZIzJbhqTbj_Java";
        int qos = 0;

        try {
            MqttClient client = new MqttClient(broker, clientid, new MemoryPersistence());
            // Connect options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);

            frame = new JFrame("Dynamic Line Graph Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            panel = new DynamicGraphicsJsonExample();

            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("connectionLost: " + cause.getMessage());
                }

                public void messageArrived(String topic, MqttMessage message)
                        throws JsonMappingException, JsonProcessingException, InterruptedException {
                    System.out.println("topic: " + topic);
                    System.out.println("Qos: " + message.getQos());
                    System.out.println("message content: " + new String(message.getPayload()));

                    String jsonString = new String(message.getPayload());

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonData = objectMapper.readTree(jsonString);

                    // Update the graph in the panel with MQTT message value
                    int lux = jsonData.get("lux").asInt();
                    panel.updateJsonData(lux);
                    frame.repaint();
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("deliveryComplete---------" + token.isComplete());
                }
            });
            frame.add(panel);
            frame.setVisible(true);

            client.connect(options);
            client.subscribe(topic, qos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

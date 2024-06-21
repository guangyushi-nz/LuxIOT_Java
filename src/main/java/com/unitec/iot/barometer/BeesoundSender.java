package com.unitec.iot.barometer;

import org.eclipse.paho.client.mqttv3.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.Random;

public class BeesoundSender {

    public static void main(String[] args) {
        String brokerUrl = "ssl://25ab2416fcd34f328cb70fb7ff3f5294.s1.eu.hivemq.cloud:8883";
        String username = "beesound";
        String password = "beesound";
        String topic = "greenhouse/bee";
        String filePath = "C:\\Users\\hippo\\Desktop\\Master-BeeKeeping\\BeeKeeping_WS\\NU\\QueenBee_Testing_15mins_64K.mp3";

        try {
            // Read the contents of the audio file
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            // Get the total size of the file
            int fileSize = fileContent.length;

            // Calculate the size of each part
            int[] partSizes = getRandomPartSizes(fileSize);

            // Create an MQTT client instance
            MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId());

            // Set up MQTT connect options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            // Connect to the MQTT broker
            client.connect(options);

            // Publish each part as a message to the topic
            int offset = 0;
            for (int i = 0; i < partSizes.length; i++) {
                int partSize = partSizes[i];
                byte[] partContent = new byte[partSize];
                System.arraycopy(fileContent, offset, partContent, 0, partSize);

                long startTime = System.currentTimeMillis();
                try {
                    // Publish the part
                    client.publish(topic, new MqttMessage(partContent));
                    long endTime = System.currentTimeMillis();
                    System.out.println("Part " + (i + 1) + " (Size: " + partSize + " bytes) published successfully in " + ((endTime - startTime) / 1000) + " seconds");
                } catch (MqttException e) {
                    // Log the error and retry or take appropriate action
                    System.err.println("Error publishing part " + (i + 1) + ": " + e.getMessage());
                }

                offset += partSize;
            }

            // Disconnect from the MQTT broker
            client.disconnect();

            System.out.println("Audio file sent successfully.");
        } catch (IOException | MqttException e) {
            e.printStackTrace();
        }
    }

    private static int[] getRandomPartSizes(int totalSize) {
        Random random = new Random();
        int numParts = (int) Math.ceil((double) totalSize / 5000000); // Calculate number of parts based on 5,000,000 bytes per part
        int[] partSizes = new int[numParts];
        int remainingSize = totalSize;
        for (int i = 0; i < numParts - 1; i++) {
            // Generate a random size for each part within the range of 3,500,000 to 4,500,000 bytes
            partSizes[i] = random.nextInt(1000001) + 3500000; // Random size between 3,500,000 and 4,500,000 bytes
            remainingSize -= partSizes[i];
        }
        // The last part takes the remaining size
        partSizes[numParts - 1] = remainingSize;
        return partSizes;
    }
}

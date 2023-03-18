package com.sabitov;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabitov.models.Ticket;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Main {
    private final static String path = "src/main/resources/tickets.json";

    public static void main(String[] args) {
        List<Ticket> tickets = parseTicketsJson(path);
        double flightTimeSum = 0;
        List<Integer> flightTime = new ArrayList<>();
        for (Ticket ticket : tickets) {
            String[] parsedDepartureDate = ticket.getDepartureDate().split("\\.");
            String[] parsedDepartureTime = ticket.getDepartureTime().split(":");
            String[] parsedArrivalTime = ticket.getArrivalTime().split(":");
            String[] parsedArrivalDate = ticket.getArrivalDate().split("\\.");

            LocalDateTime departureLocalTime = LocalDateTime.of(Integer.parseInt(parsedDepartureDate[2]), Integer.parseInt(parsedDepartureDate[1]), Integer.parseInt(parsedDepartureDate[0]), Integer.parseInt(parsedDepartureTime[0]), Integer.parseInt(parsedDepartureTime[1]));
            LocalDateTime arrivalLocalTime = LocalDateTime.of(Integer.parseInt(parsedArrivalDate[2]), Integer.parseInt(parsedArrivalDate[1]), Integer.parseInt(parsedArrivalDate[0]), Integer.parseInt(parsedArrivalTime[0]), Integer.parseInt(parsedArrivalTime[1]));
            ZonedDateTime departureZonedTime = ZonedDateTime.of(departureLocalTime, ZoneId.of("Asia/Vladivostok"));
            ZonedDateTime arrivalZonedTime = ZonedDateTime.of(arrivalLocalTime, ZoneId.of("Asia/Tel_Aviv"));
            int minutes = (int) Duration.between(departureZonedTime, arrivalZonedTime).toMinutes();
            flightTimeSum += minutes;
            flightTime.add(minutes);
        }
        Integer[] minutesArr = flightTime.toArray(new Integer[0]);
        sort(minutesArr, 0, minutesArr.length - 1);
        Integer percentil = minutesArr[(int) (minutesArr.length * 0.9)];
        double middleFlightTime = flightTimeSum / tickets.size() / 60;
        System.out.println("Percentil: " + percentil);
        System.out.println("MiddleFlightTime: " + middleFlightTime);
    }

    private static List<Ticket> parseTicketsJson(String path) {
        List<Ticket> tickets = new ArrayList<>();
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            bufferedReader.readLine();
            bufferedReader.readLine();
            String line;
            StringBuilder jsonLine = new StringBuilder("{");
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("  }")) {
                    jsonLine.append(line);
                } else {
                    jsonLine.append("}");
                    Ticket ticket = new ObjectMapper().readValue(jsonLine.toString(), Ticket.class);
                    tickets.add(ticket);
                    jsonLine.delete(0, jsonLine.length());
                    jsonLine.append("{");
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return tickets;
    }

    private static void sort(Integer[] array, int low, int high) {
        if (array.length == 0)
            return;

        if (low >= high)
            return;

        int middle = low + (high - low) / 2;
        int opora = array[middle];

        int i = low, j = high;
        while (i <= j) {
            while (array[i] < opora) {
                i++;
            }

            while (array[j] > opora) {
                j--;
            }

            if (i <= j) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }

        if (low < j)
            sort(array, low, j);
        if (high > i)
            sort(array, i, high);
    }

}

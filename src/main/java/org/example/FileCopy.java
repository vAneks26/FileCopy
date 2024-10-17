package org.example;


import it.sauronsoftware.cron4j.Scheduler;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FileCopy {

    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_DIR = "output/";

    public static void main(String[] args) {


        Scheduler cronScheduler = new Scheduler();
        System.out.println("Планировщик создан");

        // Создаем пул потоков
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        System.out.println("Пул потоков создан");


        try {
            cronScheduler.schedule("*/1 * * * *", () -> {
                System.out.println("Задача по cron расписанию запущена");
                scheduledExecutorService.execute(() -> {
                    try {
                        copyFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (Exception e) {
            System.out.println("Ошибка при планировании задачи: " + e.getMessage());
            e.printStackTrace();
        }

        // Запускаем планировщик
        cronScheduler.start();
        System.out.println("Планировщик запущен");


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {  //addShutdownHook — это механизм, который позволяет выполнять код перед завершением работы программы.
            cronScheduler.stop();
            scheduledExecutorService.shutdown();
            System.out.println("Планировщик и пул потоков остановлены.");
        }));
    }

    private static void copyFile() throws IOException {
        System.out.println(" копирование файла идет");

        File inputFile = new File(INPUT_FILE);
        if (!inputFile.exists()) {
            System.out.println("Исходный файл не найден: " + INPUT_FILE);
            return;
        }

        StringBuilder content = new StringBuilder();

        try (FileReader fr = new FileReader(inputFile); BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }


        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = now.format(formatter);


        String outputFileName = OUTPUT_DIR + "output_" + timestamp + ".txt";
        File outputFile = new File(outputFileName);


        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
            System.out.println("Создана директория: " + OUTPUT_DIR);
        }


        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.write(content.toString());
        }

        System.out.println("Создан файл: " + outputFileName);
    }
}
package org.example;

import java.io.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Ordenamiento {

    public static void main(String[] args) throws IOException {
        String archivo = "src/main/java/archivos/numeros_10k.txt";
        generarNumerosAleatorios(archivo, 10000);

        int[] arr = leerArchivo(archivo);

        Map<String, Double> tiempos = new HashMap<>();

        int[] arrBubble = arr.clone();
        tiempos.put("Bubble Sort", medirTiempo(() -> bubbleSort(arrBubble)));

        int[] arrQuick = arr.clone();
        tiempos.put("Quicksort", medirTiempo(() -> quickSort(arrQuick, 0, arrQuick.length - 1)));

        int[] arrStooge = arr.clone();
        tiempos.put("Stooge Sort", medirTiempo(() -> stoogeSort(arrStooge, 0, arrStooge.length - 1)));

        int[] arrPigeonhole = arr.clone();
        tiempos.put("Pigeonhole Sort", medirTiempo(() -> pigeonholeSort(arrPigeonhole)));

        int[] arrMerge = arr.clone();
        tiempos.put("Merge Sort", medirTiempo(() -> mergeSort(arrMerge, 0, arrMerge.length - 1)));

        int[] arrBitonic = arr.clone();
        tiempos.put("Bitonic Sort", medirTiempo(() -> bitonicSort(arrBitonic, 0, arrBitonic.length, 1)));

        List<Map.Entry<String, Double>> listaOrdenada = new ArrayList<>(tiempos.entrySet());
        listaOrdenada.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        listaOrdenada.forEach(entry -> {
            String tiempoFormateado = formatearTiempo(entry.getValue());
            System.out.printf("%s: %s%n", entry.getKey(), tiempoFormateado);
        });

        mostrarGrafica(tiempos);
    }

    public static double medirTiempo(Runnable algoritmo) {
        long start = System.nanoTime();
        algoritmo.run();
        long end = System.nanoTime();
        return (end - start) / 1_000_000_000.0;
    }

    public static String formatearTiempo(double tiempo) {
        if (tiempo < 60) {
            return String.format("%.9f segundos", tiempo);
        } else if (tiempo < 3600) {
            return String.format("%.9f minutos", tiempo / 60);
        } else {
            return String.format("%.9f horas", tiempo / 3600);
        }
    }

    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    public static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    public static void stoogeSort(int[] arr, int l, int h) {
        if (l >= h) {
            return;
        }
        if (arr[l] > arr[h]) {
            int temp = arr[l];
            arr[l] = arr[h];
            arr[h] = temp;
        }
        if (h - l + 1 > 2) {
            int t = (h - l + 1) / 3;
            stoogeSort(arr, l, h - t);
            stoogeSort(arr, l + t, h);
            stoogeSort(arr, l, h - t);
        }
    }

    public static void pigeonholeSort(int[] arr) {
        if (arr.length == 0) {
            return;
        }
        int min = arr[0], max = arr[0];
        for (int num : arr) {
            if (num < min) min = num;
            if (num > max) max = num;
        }
        int range = max - min + 1;
        int[] count = new int[range];
        for (int num : arr) {
            count[num - min]++;
        }
        int index = 0;
        for (int i = 0; i < range; i++) {
            while (count[i] > 0) {
                arr[index++] = i + min;
                count[i]--;
            }
        }
    }

    public static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;
            mergeSort(arr, left, middle);
            mergeSort(arr, middle + 1, right);
            merge(arr, left, middle, right);
        }
    }

    public static void merge(int[] arr, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;
        int[] L = new int[n1];
        int[] R = new int[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, middle + 1, R, 0, n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }
        while (i < n1) {
            arr[k++] = L[i++];
        }
        while (j < n2) {
            arr[k++] = R[j++];
        }
    }

    public static void bitonicSort(int[] arr, int low, int cnt, int dir) {
        if (cnt > 1) {
            int k = cnt / 2;
            bitonicSort(arr, low, k, 1);
            bitonicSort(arr, low + k, k, 0);
            bitonicMerge(arr, low, cnt, dir);
        }
    }

    public static void bitonicMerge(int[] arr, int low, int cnt, int dir) {
        if (cnt > 1) {
            int k = cnt / 2;
            for (int i = low; i < low + k; i++) {
                if (dir == 1 && arr[i] > arr[i + k] || dir == 0 && arr[i] < arr[i + k]) {
                    int temp = arr[i];
                    arr[i] = arr[i + k];
                    arr[i + k] = temp;
                }
            }
            bitonicMerge(arr, low, k, dir);
            bitonicMerge(arr, low + k, k, dir);
        }
    }

    public static void generarNumerosAleatorios(String nombreArchivo, int cantidad) throws IOException {
        Random random = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (int i = 0; i < cantidad; i++) {
                int numeroAleatorio = 10000000 + random.nextInt(90000000);
                writer.write(String.valueOf(numeroAleatorio));
                writer.newLine();
            }
        }
    }

    public static int[] leerArchivo(String nombreArchivo) throws IOException {
        List<Integer> numeros = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
        String linea;
        while ((linea = br.readLine()) != null) {
            numeros.add(Integer.parseInt(linea));
        }
        br.close();
        return numeros.stream().mapToInt(i -> i).toArray();
    }

    private static CategoryDataset crearDataset(Map<String, Double> tiempos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Map.Entry<String, Double>> listaOrdenada = new ArrayList<>(tiempos.entrySet());
        listaOrdenada.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        listaOrdenada.forEach(entry -> {
            dataset.addValue(entry.getValue(), "Tiempo (segundos)", entry.getKey());
        });
        return dataset;
    }

    private static void mostrarGrafica(Map<String, Double> tiempos) {
        CategoryDataset dataset = crearDataset(tiempos);
        JFreeChart grafico = ChartFactory.createBarChart("Tiempos de Ejecución de Algoritmos de Ordenamiento", "Algoritmos", "Tiempo (segundos)", dataset);
        ChartPanel panel = new ChartPanel(grafico);
        JFrame ventana = new JFrame();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.getContentPane().add(panel, BorderLayout.CENTER);
        ventana.pack();
        ventana.setVisible(true);
    }
}
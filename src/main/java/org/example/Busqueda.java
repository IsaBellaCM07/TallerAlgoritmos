package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class Busqueda {

    public static void main(String[] args) throws IOException {
        String archivoEntrada = "src/main/java/archivos/numeros_1m.txt";
        String archivoSalida = "src/main/java/archivos/numeros_ordenados.txt";
        generarNumerosAleatorios(archivoEntrada, 1000000);
        ordenarYEscribir(archivoEntrada, archivoSalida);
        int[] arr = leerArchivo(archivoSalida);
        Map<String, Double> tiempos = new HashMap<>();
        int elementoBusqueda = 15916039;
        tiempos.put("Búsqueda Lineal", medirTiempo(() -> busquedaLineal(arr, elementoBusqueda)));
        tiempos.put("Búsqueda Lineal Limitada", medirTiempo(() -> busquedaLinealLimitada(arr, elementoBusqueda, 1000)));
        tiempos.put("Búsqueda Binaria", medirTiempo(() -> busquedaBinaria(arr, elementoBusqueda)));
        tiempos.put("Búsqueda por Saltos", medirTiempo(() -> busquedaPorSaltos(arr, elementoBusqueda)));

        tiempos.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    String tiempoFormateado = formatearTiempo(entry.getValue());
                    System.out.printf("%s: %s%n", entry.getKey(), tiempoFormateado);
                });

        crearGrafica(tiempos);
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

    public static void ordenarYEscribir(String nombreArchivoEntrada, String nombreArchivoSalida) throws IOException {
        int[] numeros = leerArchivo(nombreArchivoEntrada);
        Arrays.sort(numeros);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivoSalida))) {
            for (int numero : numeros) {
                writer.write(String.valueOf(numero));
                writer.newLine();
            }
        }
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

    public static boolean busquedaLineal(int[] arr, int x) {
        for (int num : arr) {
            if (num == x) {
                return true;
            }
        }
        return false;
    }

    public static boolean busquedaLinealLimitada(int[] arr, int x, int limite) {
        for (int i = 0; i < Math.min(arr.length, limite); i++) {
            if (arr[i] == x) {
                return true;
            }
        }
        return false;
    }

    public static boolean busquedaBinaria(int[] arr, int x) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == x) {
                return true;
            }
            if (arr[mid] < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return false;
    }

    public static boolean busquedaPorSaltos(int[] arr, int x) {
        int n = arr.length;
        int paso = (int) Math.sqrt(n);
        int prev = 0;
        while (arr[Math.min(paso, n) - 1] < x) {
            prev = paso;
            paso += (int) Math.sqrt(n);
            if (prev >= n) {
                return false;
            }
        }
        for (int i = prev; i < Math.min(paso, n); i++) {
            if (arr[i] == x) {
                return true;
            }
        }
        return false;
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
        List<Map.Entry<String, Double>> listaTiempos = new ArrayList<>(tiempos.entrySet());
        listaTiempos.sort(Map.Entry.<String, Double>comparingByValue().reversed());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : listaTiempos) {
            dataset.addValue(entry.getValue(), "Tiempos", entry.getKey());
        }
        return dataset;
    }


    private static void crearGrafica(Map<String, Double> tiempos) {
        CategoryDataset dataset = crearDataset(tiempos);
        JFreeChart chart = ChartFactory.createBarChart(
                "Tiempos de Búsqueda",
                "Algoritmos",
                "Tiempo (segundos)",
                dataset
        );
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame ventana = new JFrame("Gráfica de Tiempos");
        ventana.setContentPane(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}

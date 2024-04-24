import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Convertidor {

    public static void main(String[] args) {
        // Clave de API para acceder a las tasas de cambio
        String apiKey = "1fad30f0bcc64cfb268a8969";
        // Códigos y nombres de moneda utilizados en el programa
        String[] currencyCodes = {"ARS", "BRL", "COP", "USD", "MXN"};
        String[] currencyNames = {"Peso Argentino", "Real Brasileño", "Peso Colombiano", "Dólar", "Peso Mexicano"};

        // Obtener las tasas de cambio de la API
        JsonObject rates = obtenerTasasDeCambio(apiKey);

        // Interacción con el usuario para realizar conversiones
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        while (!salir) {
            // Mostrar el menú de opciones al usuario
            mostrarMenu();
            System.out.print("Elija una opción: ");
            int opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    realizarConversion(rates, "USD", "ARS", scanner);
                    break;
                case 2:
                    realizarConversion(rates, "ARS", "USD", scanner);
                    break;
                case 3:
                    realizarConversion(rates, "USD", "BRL", scanner);
                    break;
                case 4:
                    realizarConversion(rates, "BRL", "USD", scanner);
                    break;
                case 5:
                    realizarConversion(rates, "USD", "COP", scanner);
                    break;
                case 6:
                    realizarConversion(rates, "COP", "USD", scanner);
                    break;
                case 7:
                    realizarConversion(rates, "USD", "MXN", scanner);
                    break;
                case 8:
                    realizarConversion(rates, "MXN", "USD", scanner);
                    break;
                case 9:
                    // Mostrar las tasas de cambio disponibles
                      mostrarTasasDeCambio(rates, currencyCodes);
                    break;
                case 10:
                    salir = true;
                    System.out.println("Gracias por utilizar el conversor, ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione nuevamente.");
            }
        }
    }

    // Método para obtener las tasas de cambio de la API
    private static JsonObject obtenerTasasDeCambio(String apiKey) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonParser jsonParser = new JsonParser();
                // Analizar la respuesta JSON y obtener las tasas de cambio
                return jsonParser.parse(responseBody).getAsJsonObject().getAsJsonObject("conversion_rates");
            } else {
                System.out.println("Error al obtener las tasas de cambio. Código de estado: " + response.statusCode());
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Error al realizar la solicitud: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    // Método para mostrar las tasas de cambio disponibles
    private static void mostrarTasasDeCambio(JsonObject rates, String[] currencyNames) {
        if (rates == null) {
            System.out.println("No se pudieron obtener las tasas de cambio.");
            return;
        }

        System.out.println("Tasas de cambio disponibles:");
        for (int i = 0; i < currencyNames.length; i++) {
            // Verificar si el objeto rates contiene la tasa de cambio para la moneda actual
            if (rates.has(currencyNames[i])) {
                double rate = rates.get(currencyNames[i]).getAsDouble();
                // Mostrar el nombre de la moneda y su tasa de cambio con respecto al dólar
                System.out.println((i + 1) + ") Dólar =>> " + currencyNames[i] + ": " + rate);
            } else {
                System.out.println("No se encontró la tasa de cambio para la moneda " + currencyNames[i]);
            }
        }
    }


    // Método para mostrar el menú de opciones al usuario
    private static void mostrarMenu() {
        System.out.println("*********************************************************");
        System.out.println("Bienvenido/a al conversor de moneda");
        System.out.println("By ISC. Marco");
        System.out.println("1) Dólar =>> Peso Argentino");
        System.out.println("2) Peso Argentino =>> Dólar");
        System.out.println("3) Dólar =>> Real Brasileño");
        System.out.println("4) Real Brasileño =>> Dólar");
        System.out.println("5) Dólar =>> Peso Colombiano");
        System.out.println("6) Peso Colombiano =>> Dólar");
        System.out.println("7) Dólar =>> Peso Mexicano");
        System.out.println("8) Peso Mexicano =>> Dólar");
        System.out.println("9) Ver Tasas de Cambio");
        System.out.println("10) Salir");
        System.out.println("*********************************************************");
    }

    // Método para realizar la conversión de moneda
    private static void realizarConversion(JsonObject rates, String fromCurrency, String toCurrency, Scanner scanner) {
        System.out.println("\n--- Realizar conversión de moneda ---");
        System.out.print("Ingrese el monto en " + fromCurrency + ": ");
        double amount = scanner.nextDouble();

        double fromRate = rates.get(fromCurrency).getAsDouble();
        double toRate = rates.get(toCurrency).getAsDouble();

        // Calcular el monto convertido utilizando las tasas de cambio
        double convertedAmount = amount * (toRate / fromRate);
        System.out.println("El monto convertido es: " + convertedAmount + " " + toCurrency);
    }
}
